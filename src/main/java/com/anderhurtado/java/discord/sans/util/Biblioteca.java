package com.anderhurtado.java.discord.sans.util;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.google.api.services.youtube.model.PlaylistItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca{

    public static String procesarNombreCancion(String dato){
        if(dato.startsWith("https://"))return Inicio.getID(dato);
        else return getID(dato);
    }

    public static AudioTrack[] procesarTodo(String dato){
        if(dato.startsWith("https://")){
            condicional:
            if(dato.replace("?","&").contains("&list=")){
                String listaID=null;
                String[] cort=dato.split("\\?",2);
                if(cort.length<2)break condicional;
                for(String s:cort[1].split("&")){
                    if(!s.startsWith("list="))continue;
                    listaID=s.replaceFirst("list=","");
                    if(listaID.isEmpty())break condicional;
                }if(listaID==null)break condicional;
                return Inicio.getYoutubePlayList(listaID);
            }return new AudioTrack[]{Cantante.getTrack(Inicio.getID(dato))};
        }else return new AudioTrack[]{Cantante.getTrack(getID(dato))};
    }

    public static String[] add(String ID,String... alias){
        try{
            List<String> ok=new ArrayList<>();
            for(String a:alias)if(a.length()<64&&!a.contains("\\")&&Inicio.getID(a)==null)ok.add(a);
            if(ok.isEmpty())return new String[0];
            String cds="(?,?)";
            int tot=ok.size();
            for(int x=1;x<tot;x++)cds+=",(?,?)";
            PreparedStatement ps=Inicio.getPS("INSERT IGNORE INTO Registros (Nombre,Enlace) VALUES "+cds+";");
            for(int x=0;x<tot;x++){
                ps.setString((x<<1)|1,ok.get(x));
                ps.setString((x<<1)+2,ID);
            }ps.executeUpdate();
            SQLWarning sw=ps.getWarnings();
            if(sw!=null)do{
                ok.remove(sw.getMessage().split("'")[1]);
            }while((sw=sw.getNextWarning())!=null);
            return ok.toArray(new String[0]);
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static boolean quit(String... alias){
        try{
            if(alias.length==0)return false;
            String cds="?";
            for(int x=1;x<alias.length;x++)cds+=",?";
            PreparedStatement ps=Inicio.getPS("DELETE FROM Registros WHERE Nombre IN("+cds+");");
            int x=alias.length;
            while(x>0)ps.setString(x--,alias[x]);
            return ps.executeUpdate()==alias.length;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return false;
        }
    }

    public static String getID(String alias){
        try{
            PreparedStatement ps=Inicio.getPS("SELECT Enlace FROM Registros WHERE Nombre=? UNION SELECT Enlace FROM Registros WHERE Nombre LIKE ? ORDER BY RAND() LIMIT 1;");
            ps.setString(1,alias);
            ps.setString(2,"%"+alias+"%");
            ResultSet rs=ps.executeQuery();
            if(!rs.next())return null;
            return rs.getString("Enlace");
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static byte CONTEO_ALIAS=0,CONTEO_CANCIONES=1;
    public static int[] contarCanciones(){
        try{
            ResultSet rs=Inicio.getPS("SELECT (SELECT COUNT(*) FROM Registros) AS Alias,(SELECT COUNT(DISTINCT Enlace) FROM Registros) AS Canciones;").executeQuery();
            rs.next();
            return new int[]{rs.getInt("Alias"),rs.getInt("Canciones")};
        }catch(Exception Ex){
            Ex.printStackTrace();
            return new int[2];
        }
    }

    public static String[] listarCancion(String lista,String... canciones){
        try{
            String cds="(?,?)";
            int tot=canciones.length;
            for(int x=1;x<tot;x++)cds+=",(?,?)";
            PreparedStatement ps=Inicio.getPS("INSERT IGNORE INTO Listas (Lista,Cancion) VALUES "+cds+";");
            for(int x=0;x<tot;x++){
                ps.setString((x<<1)|1,lista);
                ps.setString((x<<1)+2,canciones[x]);
            }ps.executeUpdate();
            SQLWarning sw=ps.getWarnings();
            if(sw!=null){
                List<String> buenas=new ArrayList<>();
                for(String c:canciones)buenas.add(c);
                do{
                    buenas.remove(sw.getMessage().split("'")[1]);
                }while((sw=sw.getNextWarning())!=null);
                return buenas.toArray(new String[0]);
            }return canciones;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static String[] getListas(){
        try{
            ResultSet rs=Inicio.getPS("SELECT DISTINCT Lista FROM Listas;").executeQuery();
            List<String> listas=new ArrayList<>();
            while(rs.next())listas.add(rs.getString("Lista"));
            return listas.toArray(new String[0]);
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static String[] getCancionesListadas(String lista,boolean aleatorio){
        try{
            PreparedStatement ps=Inicio.getPS("SELECT Cancion FROM Listas WHERE Lista=? ORDER BY "+(aleatorio?"RAND()":"ID ASC")+";");
            ps.setString(1,lista);
            ResultSet rs=ps.executeQuery();
            List<String> canciones=new ArrayList<>();
            while(rs.next())canciones.add(rs.getString("Cancion"));
            return canciones.toArray(new String[0]);
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static int contarListas(){
        try{
            ResultSet rs=Inicio.getPS("SELECT COUNT(DISTINCT Lista) AS Total FROM Listas;").executeQuery();
            rs.next();
            return rs.getInt("Total");
        }catch(Exception Ex){
            Ex.printStackTrace();
            return 0;
        }
    }

    public static int contarCancionesListadas(String lista){
        try{
            PreparedStatement ps=Inicio.getPS("SELECT COUNT(*) AS Total FROM Listas WHERE Lista=?;");
            ps.setString(1,lista);
            ResultSet rs=ps.executeQuery();
            rs.next();
            return rs.getInt("Total");
        }catch(Exception Ex){
            Ex.printStackTrace();
            return 0;
        }
    }

    public static boolean deslistar(String lista,String cancion){
        try{
            PreparedStatement ps=Inicio.getPS("DELETE FROM Listas WHERE Lista=? AND Cancion=?;");
            ps.setString(1,lista);
            ps.setString(2,cancion);
            return ps.executeUpdate()==1;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarLista(String nombre){
        try{
            PreparedStatement ps=Inicio.getPS("DELETE FROM Listas WHERE Lista=?;");
            ps.setString(1,nombre);
            return ps.executeUpdate()>0;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return false;
        }
    }

}
