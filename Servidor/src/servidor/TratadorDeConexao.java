package servidor;

import java.net.*;
import comando.Comando;
//import lista.desordenada.ListaDesordenada;
//import java.io.*;
import lista.ordenada.*;
import servidor.Salas;

public class TratadorDeConexao extends Thread
{
    protected Sala                  sala;
    protected ListaOrdenada<String> nick=new ListaOrdenada<String>();
    protected Usuario               usuario;
    protected boolean               fim=false;

    public void pare ()
    {
        this.fim=true;
    }

    synchronized public boolean getFim()
    {
    	return fim;
    }
    
    public void run ()
    {
    	String nick = this.usuario.getNick();
    	
    	while (!fim)
        {
        	try
        	{
		        Comando c = this.usuario.recebe();
		        
		        switch (c.getProtocolo())
		        {
		        	case "[MENSAGEM]":
		        		if (c.getDestinatario().equals("TODOS"))
		        			this.sala.msgParaTodos((String)c.getMensagem(), this.usuario.getNick());
		        		else
		        			this.sala.msgParticular((String)c.getMensagem(), this.usuario.getNick(), c.getDestinatario());
		        		break;
		        	case "[SAIR]":
		        		removerUsuario();
		        		break;
		        }
        	}
        	catch (Exception es)
        	{
        		System.out.println("Conexao com o usuario " + nick + " perdida!");
        		if (this.usuario != null)
        			removerUsuario();
        	}
        }
        
    }
    
    public void removerUsuario ()
    {
    	sala.sai(this.usuario);
    	sala.enviarProtocoloParaTodos(new Comando ("[NICK-OUT]", "Servidor", "TODOS", this.usuario.getNick()));
    	sala.msgParaTodos("Usuário desconectado - " + this.usuario.getNick(), "Servidor");
    	this.usuario = null;
    	this.fim=true;
    }

    public TratadorDeConexao (Salas salas, Socket conexao) throws Exception
    {
    	try
    	{
	        String[] nomesSalas;
	        
	        nomesSalas = salas.getNomesSalas();
	        
	        try
	        {
	        	this.usuario = new Usuario (nomesSalas, conexao);
	        }
	        catch (Exception e)
	        {
	        	System.out.println("Uma conexao com um usuario indefinido foi perdida");
	        	throw e;
	        }
	        
	
	        String nomeSala = this.usuario.getNomeSala();
	        
	        try
	        {
	        	while (salas.getSala(nomeSala).getUsuario(this.usuario.getNick()) != null)
	        	{
	        		// ja existe um usuario com aquele nome
	        		// o servidor tera que pedir para o usuario que escolha outro nome
	        		
	        		String nomeTomado = this.usuario.getNick();
	        		this.usuario.pedirOutroNome(nomeTomado);
	        	}
	        }
	        catch (SocketException e) { throw new Exception ("ERRO TratadorDeConexao: A conexao com o usuario foi perdida"); }
	        catch (Exception e) {}
	        
	        this.sala       = salas.getSala (nomeSala);
	        
	        this.usuario.envia(new Comando("[SUCESSO]", this.usuario.getNick(), "Servidor", "Usuário entrou na sala."));
	        this.usuario.envia(new Comando("[USUARIOS]", this.usuario.getNick(), "Servidor", sala.getNicks().toArray()));
	        
	        
	        
	        this.sala.entra (this.usuario);
	        
	        sala.msgParaTodos("Usuário conectado - " + this.usuario.getNick(), "Servidor");
	        sala.enviarProtocoloParaTodos(new Comando("[NICK-IN]", "Servidor", "TODOS", this.usuario.getNick()));
	        /////////////////////////////////////////////////////////
    	}
	    catch (SocketException e)
	    {
	    	throw new Exception ("Socket invalido");
	    }
    }
}