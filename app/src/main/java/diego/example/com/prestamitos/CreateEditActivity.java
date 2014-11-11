package diego.example.com.prestamitos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class CreateEditActivity extends Activity {


    protected String category;
    protected String trust;
    protected String filename;
    protected EditText editDate;
    protected Calendar myCalendar;
    protected DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);

        final EditText editNameProduct = (EditText)findViewById(R.id.editNameProduct);
        final EditText editWho = (EditText)findViewById(R.id.editNameWho);
        editDate = (EditText)findViewById(R.id.editDate);
        Button btnSave = (Button)findViewById(R.id.btnSave);
        final Bundle bundle = getIntent().getExtras();
        final String activityFrom = bundle.getString("activity");
        final String fileNameList = bundle.getString("file");
        Button btnBack = (Button)findViewById(R.id.btnBack);
        myCalendar = Calendar.getInstance();
        //Darle un valor iniciarl al radiobutton
        trust = "Fiable";

        Spinner spinnerCat = (Spinner)findViewById(R.id.spinnerCat);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(this,R.array.categoria,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RadioGroup radioTrust = (RadioGroup)findViewById(R.id.radioGroupTrust);
        radioTrust.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton asd = (RadioButton)findViewById(i);
                trust = asd.getText().toString();

            }
        });



        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new DatePickerDialog(CreateEditActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditActivity.this.finish();
            }
        });

        if (activityFrom.equals("main")) {
            filename = getNewLoanName();
        } else if (activityFrom.equals("list")) {
            filename = fileNameList;
            fillFields(editNameProduct,editWho, spinnerCat, radioTrust);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si vienes de la activity main (al añadir prestamo)
                if (activityFrom.equals("main")) {
                    if (editNameProduct.getText().toString().trim().equals("") || editWho.getText().toString().trim().equals("")) {
                        Toast.makeText(CreateEditActivity.this, "Error, introduce un producto y/o persona.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (validateDate(editDate.getText().toString())) {
                            if (parseDate()) {
                                createXML(editNameProduct.getText().toString().trim(), editWho.getText().toString().trim(), editDate.getText().toString(), category, trust, filename);
                                Toast.makeText(CreateEditActivity.this, filename+" guardado con exito", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CreateEditActivity.this, "Introduce una fecha posterior a la actual.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CreateEditActivity.this, "Error, introduce la fecha en el formato: mm/dd/yyyy", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                //Si vienes desde listar los prestamos (al mostrar el estado de los prestamos)
                if (activityFrom.equals("list")) {
                    createXML(editNameProduct.getText().toString(), editWho.getText().toString(), editDate.getText().toString(), category, trust, filename);
                    Toast.makeText(CreateEditActivity.this, filename+" guardado con exito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        editDate.setText(sdf.format(myCalendar.getTime()));

    }

    private String getNewLoanName() {
        File file = getFilesDir();
        Integer cont = new Integer(0);
        for (int i=1; i<=file.listFiles().length; i++) {
            String name = "Prestamo"+i+".xml";
            File filename = new File(file,name);
            cont++;
            if (!filename.exists()) {
                return name;
            }
        }
        cont++;


        return "Prestamo"+cont+".xml";
    }

    private void createXML(String namePr, String namePerson, String dateTo, String categoryProduct, String trustPerson, String nameFile) {
        try {
            Document doc1 = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc1.createElement("Prestamo");
            doc1.appendChild(root);
            Element nameProduct = doc1.createElement("NameProduct");
            Element nameWho = doc1.createElement("NameWho");
            Element date = doc1.createElement("Date");
            Element category = doc1.createElement("Category");
            Element trust = doc1.createElement("Trust");
            root.appendChild(nameProduct);
            nameProduct.appendChild(doc1.createTextNode(namePr));
            root.appendChild(nameWho);
            nameWho.appendChild(doc1.createTextNode(namePerson));
            root.appendChild(date);
            date.appendChild(doc1.createTextNode(dateTo));
            root.appendChild(category);
            category.appendChild(doc1.createTextNode(categoryProduct));
            root.appendChild(trust);
            trust.appendChild(doc1.createTextNode(trustPerson));

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc1);
            StreamResult result = new StreamResult(new FileOutputStream(getFilesDir()+"/"+nameFile));
            trans.transform(source, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private boolean validateDate(String registerdate) {

        String regEx ="^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
        Matcher matcherObj = Pattern.compile(regEx).matcher(registerdate);
        if (matcherObj.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private boolean parseDate() {
        boolean ok = false;
        Calendar actualCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(actualCalendar.getTime());
        String formattedDate2 = sdf.format(myCalendar.getTime());
        try {
            Date actualDate = sdf.parse(formattedDate);
            Date introducedDate = sdf.parse(formattedDate2);
            if (introducedDate.before(actualDate)) {
                ok = false;
            } else {
                ok = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ok;
    }

    private void fillFields(EditText editName, EditText editWho, Spinner spinner, RadioGroup radio) {

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(getFilesDir()+"/"+filename));
            Element arrel = (Element) doc.getChildNodes().item(0);
            editName.setText(arrel.getElementsByTagName("NameProduct").item(0).getFirstChild().getNodeValue());
            editWho.setText(arrel.getElementsByTagName("NameWho").item(0).getFirstChild().getNodeValue());
            editDate.setText(arrel.getElementsByTagName("Date").item(0).getFirstChild().getNodeValue());
            //Spinner
            String[] arraySpinner = getResources().getStringArray(R.array.categoria);
            for (int i=0; i< arraySpinner.length; i++) {
                if (arraySpinner[i].equals(arrel.getElementsByTagName("Category").item(0).getFirstChild().getNodeValue())) {
                    spinner.setSelection(i);
                }
            }
            //RadioGroup
            int count = radio.getChildCount();
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<RadioButton>();
            for (int i=0;i<count;i++) {
                View o = radio.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton)o);
                }
            }
            for (int i=0; i< listOfRadioButtons.size(); i++) {
                RadioButton asd = listOfRadioButtons.get(i);
                if (asd.getText().toString().equals(arrel.getElementsByTagName("Trust").item(0).getFirstChild().getNodeValue())) {
                    asd.setChecked(true);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }
}
