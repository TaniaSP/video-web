package mx.RosasSoto.proyectofinalvideo;

import java.io.File;
import java.io.FileOutputStream;

import java.io.InterruptedIOException;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;

public class CargaDatosWS {
	public String getClima(String pass,String id_dispositivo){
		String res=null;
		//Se crea un objeto de tipo SoapObjecto. Permite hacer el llamado al WS
		SoapObject rpc;
		rpc = new SoapObject("http://adminvideos.tk/webservice/server.php", "playlist");
		//De acuerdo a la documentacion del ws, hay 2 parametros que debemos pasar nombre de la ciuda y del pais
		//Para obtener informacion del WS , se puede consultar http://www.webservicex.net/globalweather.asmx?WSDL
		rpc.addProperty("id",id_dispositivo );
		rpc.addProperty("pass", pass);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		//envelope.bodyOut = rpc;
		//Se establece que el servicio web esta hacho en .net
		envelope.dotNet = false;
		//envelope.encodingStyle = SoapSerializationEnvelope.XSD;
		envelope.setOutputSoapObject(rpc);
		//Para acceder al WS se crea un objeto de tipo HttpTransportSE , esto es propio de la libreia KSoap
		HttpTransportSE androidHttpTransport= null;
		try {
			String conexion = "http://adminvideos.tk/webservice/server.php?wsdl";
			androidHttpTransport = new HttpTransportSE(conexion);
			androidHttpTransport.debug = true;
			//Llamado al servicio web . Son el nombre del SoapAction, que se encuentra en la documentacion del servicio web y el objeto envelope
			androidHttpTransport.call("http://adminvideos.tk/webservice/server.php#playlist", envelope);
			//Respuesta del Servicio web
			SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			
			SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
			res = response.toString();
		}catch (Exception e){
			System.out.println(e.getMessage());
			res=e.getMessage();
		}

		return res;
		
	}
	public String autentifica(String id_dispositivo, String pass){
		String res=null;
		SoapObject rpc;
		rpc = new SoapObject("http://adminvideos.tk/webservice/server.php", "autentifica");
		rpc.addProperty("id",id_dispositivo );
		rpc.addProperty("pass", pass);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE androidHttpTransport= null;
		try {
			String conexion = "http://adminvideos.tk/webservice/server.php?wsdl";
			androidHttpTransport = new HttpTransportSE(conexion);
			androidHttpTransport.debug = true;
			androidHttpTransport.call("http://adminvideos.tk/webservice/server.php#autentifica", envelope);
			Object result = envelope.getResponse();
			res=result.toString();
		}catch (Exception e){
			System.out.println(e.getMessage());
			res=e.getMessage();
		}
		return res;		
	}
	public String registra(String pass, String descripcion, String latitud, String longitud){
		String res=null;
		SoapObject rpc;
		rpc = new SoapObject("http://adminvideos.tk/webservice/server.php", "registrarse");
		rpc.addProperty("pass",pass );
		rpc.addProperty("descripcion", descripcion);
		rpc.addProperty("latitud", latitud);
		rpc.addProperty("longitud", longitud);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE androidHttpTransport= null;
		try {
			String conexion = "http://adminvideos.tk/webservice/server.php?wsdl";
			androidHttpTransport = new HttpTransportSE(conexion);
			androidHttpTransport.debug = true;
			androidHttpTransport.call("http://adminvideos.tk/webservice/server.php#registrarse", envelope);
			Object result = envelope.getResponse();
			res=result.toString();
		}catch (Exception e){
			System.out.println(e.getMessage());
			res=e.getMessage();
		}
		return res;		
	}
	public String getPlaylist(String pass,String id_dispositivo){
		String res=null;
		SoapObject rpc;
		System.setProperty("http.keepAlive", "false");
		rpc = new SoapObject("http://adminvideos.tk/webservice/server.php", "playlist");
		rpc.addProperty("id",id_dispositivo);
		rpc.addProperty("pass", pass);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE androidHttpTransport= null;
		try {
			String conexion = "http://adminvideos.tk/webservice/server.php?wsdl";
			androidHttpTransport = new HttpTransportSE(conexion);
			androidHttpTransport.debug = true;
			androidHttpTransport.call("http://adminvideos.tk/webservice/server.php#playlist", envelope);
			//System.setProperty("http.keepAlive", "false");
			//Object result = envelope.getResponse();
			//res=result.toString();
			SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			res = resultsRequestSOAP.toString();
		}catch (Exception e){
			System.out.println(e.getMessage());
			res=e.getMessage();
		}

		return res;
		
	}
	public String reproduccion(String id_dispositivo,String pass, String id_video, String hora, String fecha){
		String res=null;
		SoapObject rpc;
		rpc = new SoapObject("http://adminvideos.tk/webservice/server.php", "reproduccion");
		rpc.addProperty("id_dispositivo",id_dispositivo );
		rpc.addProperty("pass", pass);
		rpc.addProperty("id_video", id_video);
		rpc.addProperty("hora", hora);
		rpc.addProperty("fecha", fecha);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE androidHttpTransport= null;
		try {
			String conexion = "http://adminvideos.tk/webservice/server.php?wsdl";
			androidHttpTransport = new HttpTransportSE(conexion);
			androidHttpTransport.debug = true;
			androidHttpTransport.call("http://adminvideos.tk/webservice/server.php#reproduccion", envelope);
			Object result = envelope.getResponse();
			res=result.toString();
		}catch (Exception e){
			System.out.println(e.getMessage());
			res=e.getMessage();
		}
		return res;		
	}
	
}