package servidor;

import comando.*;

import java.net.*;
import java.io.*;

public class Usuario //implements Cloneable
{
    protected String             nomeSala;
    protected String             nick;
    protected Socket             conexao;
    protected ObjectInputStream  receptor;
    protected ObjectOutputStream transmissor;

    
    ///////////////////////////////////////////////////////////////////
    
    // construtor de usuario instancia BufferedReader e PrintWriter, 
    // interage atraves deles com o usuario para enviar a lista de
    // salas e obter a sala onde o usuario quer entrar, bem como o
    // seu nick, inicializando this.sala e this.nick
    public Usuario (String[] nomSls, Socket conexao) throws Exception
    {
    	if (nomSls == null)
    		throw new IllegalArgumentException("ERRO Usuario: Nao foi passada uma lista de salas para o usuario.");
    	if (conexao == null)
    		throw new IllegalArgumentException("ERRO Usuario: Conexao passada eh nula");
    	
    	this.conexao = conexao;
    	this.receptor = new ObjectInputStream(conexao.getInputStream());
    	this.transmissor = new ObjectOutputStream(conexao.getOutputStream());
    	
    	
    	Comando resp;
    	
    	
    	
    	// CONSEGUIR SALA QUE O USUARIO QUER ENTRAR
    	/////////////////////////////////////////////////
    	envia(new Comando("[MENSAGEM]", "Usuario", "Servidor", "Escolha a sala que deseja entrar"));
    	
    	envia(new Comando("[SALAS]", "Usuario", "Servidor", nomSls));
    	
    	for (;;)
    	{
    		resp = recebe();
    		
    		if (!resp.getProtocolo().equals("[MENSAGEM]") || !(resp.getMensagem() instanceof Integer))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Comando inválido!"));
    			continue;
    		}
    		
    		Integer numero = (Integer)resp.getMensagem();
    		if (numero < 0 || numero >= nomSls.length)
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Código da sala inválido!"));
    			continue;
    		}
    		
    		this.nomeSala = nomSls[numero];
    		break;
    	}
    	/////////////////////////////////////////////////
    	
    	// CONSEGUIR NOME DO USUARIO
    	//////////////////////////////////////////////////
    	envia(new Comando("[MENSAGEM]", "Usuario", "Servidor", "Por favor, envie o seu nome de usuario."));
    	
    	for (;;)
    	{
    		resp = recebe();
    		
    		if (!resp.getProtocolo().equals("[ENTRAR]") || !(resp.getMensagem() instanceof String))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Comando inválido!"));
    			continue;
    		}
    		
    		String mensagem = (String)resp.getMensagem();
    		
    		if (mensagem.equals("Servidor") || mensagem.equals("TODOS") || mensagem.equals("/sair") || mensagem.equals(""))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Nome de usuário inválido! Escolha outro nome."));
    			continue;
    		}
    		
    		this.nick = (String)resp.getMensagem();
    		envia(new Comando("[SUCESSO]", "Usuario", "Servidor", "Nome de usuario válido (por enquanto)"));
    		
    		break;
    	}
    	//////////////////////////////////////////////////
    }
    
    synchronized public void pedirOutroNome (String nomeQueJaExiste)
    {
    	Comando resp;
    	
    	// CONSEGUIR NOME DO USUARIO
    	envia(new Comando("[ERRO]", "Usuario", "Servidor", "Seu nome de usuario ja existe, escolha outro nome."));
    	
    	for (;;)
    	{
    		resp = recebe();
    		
    		if (!resp.getProtocolo().equals("[ENTRAR]") || !(resp.getMensagem() instanceof String))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Comando inválido!"));
    			continue;
    		}
    		
    		String mensagem = (String)resp.getMensagem();
    		
    		if (mensagem.equals("Servidor") || mensagem.equals("TODOS") || mensagem.equals("/sair"))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Nome de usuário inválido!"));
    			continue;
    		}
    		
    		this.nick = (String)resp.getMensagem();
    		
    		if (mensagem.equals(nomeQueJaExiste))
    		{
    			envia(new Comando("[ERRO]", "Usuario", "Servidor", "Seu nome de usuario ja existe, escolha outro nome."));
    			continue;
    		}
    		
    		break;
    	}
    }

    // getters para nomeSala e nick
    synchronized public String getNomeSala ()
    {
    	return this.nomeSala;
    }
    
    synchronized public String getNick ()
    {
    	return this.nick;
    }
    
    synchronized public void setNick (String novoNick)
    {
    	this.nick = novoNick;
    }
    
    // metodo para desconectar
    public void desconectar ()
    {
    	envia(new Comando("[SAIR]", this.nick, "Servidor", "Você foi desconectado."));
    	
    	try
    	{
	    	this.transmissor.close();
	    	this.receptor.close();
	    	this.conexao.close();
    	}
    	catch (IOException e) {}
    	catch (Exception e) {}
    }
    
    // recebe do usuario, usando this.receptor
    public Comando recebe ()
    {
    	try
    	{
    		return (Comando)receptor.readObject();
    	}
    	catch (IOException e) { }
    	catch (ClassNotFoundException e)
    	{
    		envia(new Comando("[ERRO]", "Usuario", "Servidor", "Comando invalido"));
    	}
    	
    	return null;
    }

    // envia para o usuario, usando this.transmissor
    public void envia (Comando c)
    {
    	try
    	{
	    	if (c == null)
	    		throw new IllegalArgumentException("ERRO Usuario: Comando a enviar nulo.");
	    	
	    	transmissor.writeObject(c);
			transmissor.flush();
    	}
    	catch (IOException e) { }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////

    // metodos obrigatorios
    synchronized public String toString ()
    {
    	String ret = "{";
    	
    	ret += "NICK: " + this.nick + "; ";
    	ret += "SALA: " + this.nomeSala + "; ";
    	ret += "CONEXAO: " + this.conexao.toString() + "; ";
    	ret += "RECEPTOR: " + this.receptor.toString() + "; ";
    	ret += "TRANSMISSOR: " + this.transmissor.toString() + "}";
    	
    	return ret;
    }
    
    synchronized public boolean equals (Object obj)
    {
    	if (obj == null)
    		return false;
    	
    	if (obj == this)
    		return true;
    	
    	if(!obj.getClass().equals(this.getClass()))
    		return false;
    				
    	Usuario u = (Usuario)obj;
    	if(!u.nomeSala.equals(this.nomeSala))
    		return false;
    	
    	if (!u.conexao.equals(this.conexao))
    		return false;
    	
    	if(!u.nomeSala.equals(this.nomeSala))
    		return false;
    	
    	if(!u.receptor.equals(this.receptor))
    		return false;
    	
    	return u.transmissor.equals(this.transmissor);
    }
    
    synchronized public int hashCode ()
    {
    	int ret = 24;
    	
    	ret += ret * 2 + nick.hashCode();
    	ret += ret * 2 + nomeSala.hashCode();
    	ret += ret * 2 + conexao.hashCode();
    	ret += ret * 2 + receptor.hashCode();
    	ret += ret * 2 + transmissor.hashCode();
    	
    	return ret;
    }
    
    /*
    public Usuario (Usuario u)
    {
    	if (u == null)
    		throw new IllegalArgumentException("ERRO Usuario: Usuario a copiar eh nulo");
    	
    	this.nick = u.nick;
    	this.nomeSala = u.nomeSala;
    	this.conexao = u.conexao;
    	try
    	{
	    	this.receptor = new ObjectInputStream(conexao.getInputStream());
	    	this.transmissor = new ObjectOutputStream(conexao.getOutputStream());
    	}
    	catch (Exception e) {}
    }
    
    public Object clone ()
    {
    	Usuario ret = null;
    	
    	ret = new Usuario(this);		
    	
    	return ret;
    }
    */
}