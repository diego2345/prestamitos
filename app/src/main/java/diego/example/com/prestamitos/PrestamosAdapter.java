package diego.example.com.prestamitos;

import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PrestamosAdapter extends BaseAdapter {
   private ArrayList<String> versiones;
   private LayoutInflater mInflater;
   private File dir;
   public PrestamosAdapter(Context context, ArrayList<String> vers, File dir) {
      this.mInflater = LayoutInflater.from(context);
      this.versiones = vers;
       this.dir = dir;
   }
   public int getCount() {
      return versiones.size();
   }
   public String getItem(int position) {
      return versiones.get(position); 
   }
   public long getItemId(int position) {
      return position;
   }
   public View getView(final int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;
      if (convertView == null) {
         convertView = mInflater.inflate(R.layout.list, null);
         holder = new ViewHolder();
         holder.hNombre = (TextView) convertView.findViewById(R.id.idNombre);
         holder.hWho = (TextView) convertView.findViewById(R.id.idWho);

         convertView.setTag(holder);
      } else {
         holder = (ViewHolder) convertView.getTag();
      }


       String version = getItem(position);

       try {
           Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(dir+"/"+version));
           Element arrel = (Element) doc.getChildNodes().item(0);
           holder.hNombre.setText(arrel.getElementsByTagName("NameProduct").item(0).getFirstChild().getNodeValue());
           holder.hWho.setText(arrel.getElementsByTagName("NameWho").item(0).getFirstChild().getNodeValue());

           //Comprobar fechas
           Calendar actualCalendar = Calendar.getInstance();
           SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
           String actualDateString = sdf.format(actualCalendar.getTime());
           String dateLoanString = arrel.getElementsByTagName("Date").item(0).getFirstChild().getNodeValue();


           Date actualDateDate = sdf.parse(actualDateString);
           Date dateLoanDate = sdf.parse(dateLoanString);
            //Aplicar con estilos
           if (diferenciaEnDias2(dateLoanDate, actualDateDate) >= 3) {
               convertView.setBackgroundResource(R.color.verdecito);
           }
           if (diferenciaEnDias2(dateLoanDate, actualDateDate) == 2) {
               convertView.setBackgroundResource(R.color.amarillito);
           }
           if (diferenciaEnDias2(dateLoanDate, actualDateDate) == 1) {
               convertView.setBackgroundResource(R.color.naranjito);
           }
           if (diferenciaEnDias2(dateLoanDate, actualDateDate) <= 0) {
               convertView.setBackgroundResource(R.color.rojito);
           }


       } catch (SAXException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (ParserConfigurationException e) {
           e.printStackTrace();
       } catch (ParseException e) {
           e.printStackTrace();
       }




      return convertView;
   }

    public static int diferenciaEnDias2(Date fechaMayor, Date fechaMenor) {
        long diferenciaEn_ms = fechaMayor.getTime() - fechaMenor.getTime();
        long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
        return (int) dias;
    }

   class ViewHolder {
      TextView hNombre;
      TextView hWho;
   }
}
