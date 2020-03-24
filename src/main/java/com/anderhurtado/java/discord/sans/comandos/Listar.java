package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Listar extends Comando{

    public Listar(){
        super("Añadir una canción a una lista","listar","list");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<3){
            e.getChannel().sendMessage(Boton.NUBE_BOCADILLO+" Uso: "+args[0]+" <Nombre de la lista> <Canciones...>").complete();
            return;
        }if(args[1].length()>64){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" El nombre de la lista es demasiado largo.").complete();
            return;
        }if(args[1].startsWith("-")){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" ¡El nombre de una lista no puede empezar por '-'!").complete();
            return;
        }List<String> canciones=new ArrayList<>(),error=new ArrayList<>();
        for(int x=2;x<args.length;x++){
            String c=Biblioteca.procesarNombreCancion(args[x]);
            if(c==null)error.add(c);
            else canciones.add(c);
        }if(!error.isEmpty()){
            String[] errores=error.toArray(new String[0]);
            String msg=errores[0];
            for(int x=1;x<errores.length-1;x++)msg+=", '"+errores[x]+"'";
            e.getChannel().sendMessage(Boton.ADVERTENCIA+" No conozco el significado de "+msg+" ni de "+errores[errores.length-1]+"."+(canciones.size()>0?" Pero haré lo que pueda con el resto...":"")).complete();
            if(canciones.isEmpty())return;
        }String[] resultado=Biblioteca.listarCancion(args[1],canciones.toArray(new String[0]));
        e.getChannel().sendMessage(Boton.FLOPPY_DISK+" ¡Se han guardado las canciones!").complete();
    }
}
