package com.anderhurtado.java.discord.sans.objetos;

import com.anderhurtado.java.discord.sans.comandos.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;

public abstract class Comando{

    final static HashMap<String,Comando> comandos=new HashMap<>();

    public final static Comando[] COMANDOS=new Comando[]{new Ayuda(),new Agregar(),new Eliminar(),new Reproducir(),new Buscar(),new Alias(),new Cantando(),new Aleatorio(),new Detener(),new Saltar(),
            new Cola(),new Listar(),new Deslistar(),new PlayList()};

    static{
        new Perdon();
    }

    public static void ejecutar(GuildMessageReceivedEvent e,String[] args){
        if(comandos.containsKey(args[0]))new Thread(()->comandos.get(args[0]).ejecuta(e,args)).start();
    }

    public final String[] nombres;
    public final String descripcion;

    public Comando(String ayuda,String... cmds){
        descripcion=ayuda;
        nombres=cmds;
        for(String n:cmds)comandos.put("/"+n.toLowerCase(),this);
    }

    public abstract void ejecuta(GuildMessageReceivedEvent e,String[] args);
}
