package servidor;

import lista.desordenada.ListaDesordenada;
import lista.ordenada.ListaOrdenada;
import servidor.Usuario;

public class Salas
{
    protected int                    qtd;
    protected ListaDesordenada<Sala> salas;

    public Salas ()
    {
    	qtd = 0;
    	this.salas = new ListaDesordenada<Sala>();
    }
    
    synchronized public int getQtd()
    {
    	return this.qtd;
    }
    
    synchronized public void novaSala (String nome)
    {
    	if (nome==null)
    		throw new IllegalArgumentException("ERRO Salas: Nome de uma sala nova eh nulo ou vazio");
    	if (nome.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: Nome de uma sala nova eh nulo ou vazio");
    
    	try
    	{
    		salas.reiniciarSelecionado();
    		while (salas.lerProx())
    		{
    			if (salas.getItem().getNome().equals(nome))
    			{
    				System.out.println("ERRO Salas: Ja existe uma sala com esse nome");
    				return;
    			}
    		}
    		
    		salas.insiraNoFim(new Sala(nome));
    	}
    	catch (Exception e) {}
    	
    	System.out.println("Sala " + nome + " criada.");
    	
    	qtd++;
    }
    
    public void novoUsuario (String nomSal, Usuario usr) throws Exception
    {
    	if (nomSal == null || usr == null)
    		throw new IllegalArgumentException("ERRO Salas: Um dos parametros para novo usuario eh nulo");
    	if (nomSal.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: Um dos parametros para novo usuario eh vazio");
    	
    	Sala sal = null;
    	
    	synchronized (salas)
    	{
	    	salas.reiniciarSelecionado();
	    	while (salas.lerProx())
	    	{
	    		if (salas.getItem().getNome() == nomSal)
	    		{
	    			sal = salas.getItem();
	    			break;
	    		}
	    	}
    	}
    	
    	if (sal == null)
    		throw new Exception ("ERRO Sala: Sala a colocar usuario nao encontrada");
    
    	sal.entra(usr);
    }

    synchronized public Sala getSala (String nome) throws Exception 
    {
    	if (nome == null)
    		throw new IllegalArgumentException("ERRO Salas: Parametro para achar sala eh nulo");
    	
    	salas.reiniciarSelecionado();
    	while (salas.lerProx())
    	{
    		if (salas.getItem().nome.equals(nome))
    			return salas.getItem();
    	}
    	
    	throw new Exception("ERRO Salas: Sala nao encontrada");
    }
    
    synchronized public String [] getNomesSalas () throws Exception
    {
    	if (qtd == 0)
    		throw new Exception("ERRO Salas: Nao ha nenhuma sala criada");
    	
    	String[] ret = new String[qtd];
    	
    	salas.reiniciarSelecionado();
    	int i=0;
    	while (salas.lerProx())
    	{
    		ret[i] = salas.getItem().nome;
    		i++;
    	}
    	
    	return ret;
    }
    
    synchronized public Usuario[] getUsuariosSala (String nomSal) throws Exception
    {
    	if (nomSal == null)
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh nulo");
    	if (nomSal.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh vazio");
    
    	Sala sal = getSala(nomSal);
    	
    	ListaOrdenada<String> nicks = sal.getNicks();
    	Usuario[] usuarios = new Usuario[sal.qtdUsuarios];
    	
    	nicks.reiniciarSelecionado();
    	int i=0;
    	while (nicks.lerProx())
    	{
    		usuarios[i] = sal.getUsuario(nicks.getItem());
    		i++;
    	}
    	
    	return usuarios;
    }
    
    synchronized public Usuario getUsuario (String nomSal, String nicUsr) throws Exception
    {
    	if (nomSal == null)
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh nulo");
    	if (nomSal.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh vazio");
    	
    	if (nicUsr == null)
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome do usuario a ser procurado eh nulo");
    	if (nicUsr.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome do usuario a ser procurado eh vazio");
    	
    	Sala sal = getSala (nomSal);
    	Usuario usr = sal.getUsuario(nicUsr);
    	
    	return usr;
    }

    public void tiraUsuario (String nomSal, String nicUsr) throws Exception
    {
    	if (nomSal == null)
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh nulo");
    	if (nomSal.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome da sala a ser procurada eh vazio");
    	
		if (nicUsr == null)
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome do usuario a ser procurado eh nulo");
    	if (nicUsr.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Salas: O parametro com o nome do usuario a ser procurado eh vazio");
    	
    	Sala sal;
    	Usuario usr;
    	
    	synchronized (this)
    	{
    		sal = getSala (nomSal);
    		usr = getUsuario (nomSal, nicUsr);
    	}
    	
    	sal.sai(usr);
    }
    
    public void fechaSala (String nome) throws Exception
    {
    	Sala sal = getSala (nome);
    	
    	sal.msgParaTodos("ESTA SALA ESTÁ SENDO FECHADA", "Servidor");
    	
    	Usuario[] users = getUsuariosSala(nome);
    	
    	for (Usuario usuario : users)
    		sal.sai(usuario);

    	synchronized (salas)
    	{
	    	salas.reiniciarSelecionado();
	    	while (salas.lerProx())
	    	{
	    		if (salas.getItem().getNome().equals(sal.getNome()))
	    		{
	    			salas.removerSelecionado();
	    			break;
	    		}
	    	}
    	}
    	
    	System.out.println("Sala " + nome + " fechada.");
    }

    //////////////////////////////////////////////////////////////////////
    
    synchronized public String toString()
    {
    	String ret = "{SALAS: " + this.salas.toString();
    	ret += "; QTD_Salas: " + Integer.toString(this.qtd);
    	ret += "}";
    	
    	return ret;
    }
    
    synchronized public int hashCode()
    {
    	int ret = 7;
    	ret = ret * 3 + salas.hashCode();
    	ret = ret * 3 + new Integer(qtd).hashCode();
    	
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
    				
    	Salas salasComparadas = (Salas)obj;
    	
    	if (!salasComparadas.salas.equals(this.salas))
    		return false;
    	
    	if (salasComparadas.qtd != this.qtd)
    		return false;
    	
    	return true;
    }
    
}