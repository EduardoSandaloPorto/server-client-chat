package comando;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Comando implements Serializable
{
	private String protocolo;
	private String destinatario;
	private String remetente;
	private Object mensagem;
	
	/*
	 * 		LISTA DE PROTOCOLOS
	 * -	[MENSAGEM] : Mensagem padrão (Destinatario + Remetente + Mensagem)
	 * -    [ERRO]     : Demonstra que algo de errado aconteceu
	 * -    [SUCESSO]  : Demonstra que nao ocorreu erros e o programa pode continuar
	 * -    [SALAS]    : O Servidor está enviando a lista de salas para o usuário
	 * -    [ENTRAR]   : Usuario pedindo pra entrar
     * -    [SAIR]     : Mensagem que obriga o usuário a sair da sala
     * -    [USUARIOS] : Envia uma lista com todos os usuarios da sala
     * 
     * -    [NICK-IN]  : Passa nickname de usuario que entrou na sala;
     * - 	[NICK-OUT] : Passa nickname de usuario que saiu da sala;

	 */
	
	public Comando (String prot, String dest, String rem, Object mens)
	{		
		if (prot == null || dest == null || rem == null || mens == null)
			throw new IllegalArgumentException ("ERRO Comando: Um dos parametros eh nulo");
		
		this.protocolo    = prot;
		this.destinatario = dest;
		this.remetente    = rem;
		this.mensagem     = mens;
	}
	
	public String getProtocolo ()
	{
		return this.protocolo;
	}
	
	public String getDestinatario ()
	{
		return this.destinatario;
	}
	
	public String getRemetente ()
	{
		return this.remetente;
	}
	public Object getMensagem ()
	{
		return this.mensagem;
	}
	
	public int hashCode ()
	{
		int ret = 7;
		
		ret = ret * 3 + protocolo.hashCode();
		ret = ret * 3 + destinatario.hashCode();
		ret = ret * 3 + remetente.hashCode();
		ret = ret * 3 + mensagem.hashCode();
		
		return ret;
	}
	
	public String toString ()
	{
		String ret = "{PROTOCOLO: ";
		ret += this.protocolo + "; DESTINATARIO: ";
		ret += this.destinatario + "; REMETENTE: ";
		ret += this.remetente + "; MENSAGEM: ";
		ret += this.mensagem + "}";
		return ret;
		
	}
	
	public boolean equals (Object obj)
	{
		if(obj == null)
			return false;
		
		if(obj == this)
			return true;
		
		if(!obj.getClass().equals(this.getClass()))
			return false;
		
		Comando cmd = (Comando)obj;
		
		if(!this.protocolo.equals(cmd.protocolo))
			return false;
		
		if(!this.destinatario.equals(cmd.destinatario))
			return false;
		
		if (!this.remetente.equals(cmd.remetente))
			return false;
		
		if (!this.mensagem.equals(cmd.mensagem))
			return false;
		
		return true;
	}
}
