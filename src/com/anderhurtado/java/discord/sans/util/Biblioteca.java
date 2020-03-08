package com.anderhurtado.java.discord.sans.util;

import com.anderhurtado.java.discord.sans.Inicio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class Biblioteca{

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

}
