package servidor;

import comando.Comando;
import lista.desordenada.ListaDesordenada;
import lista.ordenada.ListaOrdenada;
import servidor.Usuario;


public class Sala
{
    protected String                    nome;
    protected int                       qtdUsuarios=0;
    protected ListaDesordenada<Usuario> usuarios;

    public Sala (String nomeSala)
    {
    	if (nomeSala == null || nomeSala.trim().equals(""))
    		throw new IllegalArgumentException("ERRO Sala: Nome de sala nao pode ser vazio");
    	
    	this.nome = nomeSala;
    	this.usuarios = new ListaDesordenada<Usuario>();
    }
    
    synchronized public Usuario getUsuario (String nick) throws Exception
    {
        if (nick == null || nick.trim().equals(""))
            throw new Exception ("ERRO Sala: Nickname a ser achado nulo");
        
        Usuario usr = null;
        usuarios.reiniciarSelecionado();
        while (usuarios.lerProx())
        {
        	if (usuarios.getItem().getNick().equals(nick))
        	{
        		usr = usuarios.getItem();
        		break;
        	}
        }
        
        if (usr==null)
        	throw new Exception ("ERRO Sala: Usuario procurado nao encontrado");
        
        return usr;
    }
    
    synchronized public ListaOrdenada<String> getNicks ()
    {
    	try
    	{
	        ListaOrdenada<String> ret = new ListaOrdenada<String>();
	        
	        usuarios.reiniciarSelecionado();
	        while(usuarios.lerProx())
	            ret.insira(usuarios.getItem().getNick());
	                
	        return ret;
    	}
    	catch (Exception e) {return null;}
    }
    
    synchronized public void entra (Usuario usr)
    {
    	if (usr==null)
    		throw new IllegalArgumentException("ERRO Sala: Usuario invalido a ser registrado");
    	
    	try
    	{
    		usuarios.insiraNoInicio(usr);
    	}
    	catch (Exception e) {}
    	qtdUsuarios++;
    }
    
    synchronized public void sai (Usuario usr)
    {
    	if (usr == null)
    		throw new IllegalArgumentException("ERRO Sala: Usuario invalido a ser retirado");
    	
    	Usuario user=null;
    	try
    	{
    		user = getUsuario(usr.getNick());
    	}
    	catch (Exception e) {return;}
    	
    	usuarios.reiniciarSelecionado();
    	usuarios.lerProx();
    	while (!usuarios.getItem().getNick().equals(user.getNick()))
    		usuarios.lerProx();
    	
    	user.desconectar();
    	usuarios.removerSelecionado();
    	usuarios.reiniciarSelecionado();
    	
    	qtdUsuarios--;
    }
    
    synchronized public void msgParaTodos (String mensagem, String remetente)
    {
    	if (mensagem==null || remetente==null)
    		return;
    	// O PROGRAMA EXECUTA RETURN PARA EVITAR QUE O USUARIO CAUSE O SERVIDOR A TRAVAR
    	
    	usuarios.reiniciarSelecionado();
    	while (usuarios.lerProx())
    		try {
    			usuarios.getItem().envia(new Comando("[MENSAGEM]", "TODOS", remetente, remetente + ": " + mensagem));
    		} catch (Exception e) {}
    }
    
    synchronized public void enviarProtocoloParaTodos (Comando c)
    {
    	if (c.getProtocolo()==null || c.getMensagem()==null || !(c.getMensagem() instanceof String))
    		return;
    	
    	usuarios.reiniciarSelecionado();
    	while (usuarios.lerProx())
    		try {
    			usuarios.getItem().envia(c);
    		} catch (Exception e) {}
    	
    }
    
    synchronized public void msgParticular (String mensagem, String remetente, String destinatario)
    {
    	if (mensagem==null || remetente==null || destinatario==null)
    		return;
    	// O PROGRAMA EXECUTA RETURN PARA EVITAR QUE O USUARIO CAUSE O SERVIDOR A TRAVAR
    	
    	try
    	{
    		Usuario usr = getUsuario(destinatario);
    		usr.envia(new Comando ("[MENSAGEM]", usr.getNick(), remetente, "(" + remetente + " - Mensagem Particular para " + usr.getNick() + "): " + mensagem));
    	}
    	catch (Exception e)
    	{
    		return;
    	}
    }
    
    synchronized public String getNome ()
    {
        return this.nome;
    }
    
    synchronized public void setNome (String n)
    {
        if (n == null)
            throw new IllegalArgumentException("ERRO Sala: Nome da sala nao pode ser nulo ou vazio");
        if (n.trim().equals(""))
            throw new IllegalArgumentException("ERRO Sala: Nome da sala nao pode ser nulo ou vazio");
        
        this.nome = n;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    synchronized public int hashCode ()
    {
       int ret = 24;
        
        ret += ret * 2 + new Integer(qtdUsuarios).hashCode();
        ret += ret * 2 + nome.hashCode();
        ret += ret * 2 + usuarios.hashCode();
        
        return ret;
    }
    
    synchronized public String toString()
    {
        String ret = "{";
        
        ret += "Nome: " + nome + "; ";
        ret += "Qtd Usuarios: " + qtdUsuarios + "; ";
        ret += "Usuarios: " + usuarios + "";
        
        ret += "}";
        return ret;
    }
    
    synchronized public boolean equals (Object obj)
    {
        if (obj == null)
            return false;
        
        if (obj == this)
            return true;
        
        if (!obj.getClass().equals(this.getClass()))
            return false;
        
        Sala s = (Sala)obj;
        
        if (!s.nome.equals(this.nome))
            return false;
        
        if (s.qtdUsuarios != this.qtdUsuarios)
            return false;
        
        if (!s.usuarios.equals(this.usuarios))
            return false;
        
        return true;
    }
}
    
    
  