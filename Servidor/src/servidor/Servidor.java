package servidor;

import java.io.*;
import java.net.*;

import lista.desordenada.ListaDesordenada;

public class Servidor
{
    public static void main (String[] args)
    {
    	try
    	{
	        Salas        salas  = new Salas ();
	        ServerSocket pedido = new ServerSocket (17170);
	
	        System.out.println ("Servidor operante...");
	        System.out.println ("IP: " + InetAddress.getLocalHost().getHostAddress() + " Porta: 17170");
	        System.out.println ("Digite 'help' para ver os comandos possiveis");
	        System.out.println ("De o comando 'shutdown' para derrubar o servidor");
	        System.out.println ();
	        
	        salas.novaSala("Sala inicial");
	        
	        ListaDesordenada<TratadorDeConexao> lista = new ListaDesordenada<TratadorDeConexao>();
	        // lista que guarda o TratadorDeConexao de cada usuario
	        
	        Thread rodar = new Thread () {
	        	public void run() {
			        for(;;)
			        {
			            Socket conexao;
						try {
							conexao = pedido.accept();
							System.out.println("Conexao recebida");
						} catch (IOException e1) {
							//System.out.println(e1);
							break;
						}
			            try
			            {
			            	Thread colocarUsuario = new Thread()
			            	{
			            		public void run() {
			            			try
			            			{
						            	TratadorDeConexao tratador = new TratadorDeConexao (salas,conexao);
						            	tratador.start();
						            	lista.insiraNoInicio(tratador);
			            			}
			            			catch (Exception e) { /* System.out.println(e); */ }
			            		}
			            	};
			            	
			            	colocarUsuario.start();
			            	continue;
			            }
			            catch (Exception e) {System.out.println(e);}
			        }
	        	}
	        };
	        
	        rodar.start();
	        
	        
	        BufferedReader leitor = new BufferedReader(new InputStreamReader(System.in));
	        
	        // COMANDOS QUE PODEM SER EXECUTADOS PELO SERVER ADMIN
	        boolean fim = false;
	        while (!fim)
	        {
	        	System.out.print("> ");
	        	String lido[] = leitor.readLine().split(" ");
	        	String comando = lido[0];
	        	
	        	switch (comando)
	        	{
	        		case "shutdown":
	        			System.out.println("Tem certeza que deseja fechar o servidor? (S/N)");
	        			if (leitor.readLine().toUpperCase().equals("S"))
	        			{
	        				System.out.println("Tem certeza mesmo? (S/N)");
	        				if (leitor.readLine().toUpperCase().equals("S"))
	        				{
	        					System.out.println("Fechando o servidor...");
	        					rodar.interrupt();
	        					lista.reiniciarSelecionado();
	        					while (lista.lerProx())
	        					{
	        						if (!lista.getItem().getFim())
	        							lista.getItem().removerUsuario();
	        					}
	        					pedido.close();
	        					
	        					System.out.println("Servidor fechado.");
	        					fim = true;
	        				}
	        			}
	        			break;
	        	
	        		case "usuarios":
	        			try
	        			{
	        				String salaAAchar="";
	        				for (String palavra : lido)
	        					salaAAchar += palavra + " ";
	        				salaAAchar = salaAAchar.substring(9, salaAAchar.length()-1);
	        				System.out.println(salas.getSala(salaAAchar).getNicks());
	        			}
	        			catch(Exception e) {System.out.println("Nao foi possivel achar esta sala");}
	        			break;
	        		case "salas":
	        			try {
	        			for (String nominho : salas.getNomesSalas())
	        				System.out.println(nominho);
	        				System.out.println(salas.salas);
	        			}
	        			catch (Exception e) {}
	        			break;
	        		case "criarsala":
	        			try
	        			{
	        				String salaACriar="";
	        				for (String palavra : lido)
	        					salaACriar += palavra + " ";
	        				salaACriar = salaACriar.substring(10, salaACriar.length()-1);
	        				salas.novaSala(salaACriar);
	        			}
	        			catch(Exception e) {System.out.println("Nao foi possivel criar esta sala");}
	        			break;
	        		case "fecharsala":
	        			try
	        			{
	        				String salaAFechar="";
	        				for (String palavra : lido)
	        					salaAFechar += palavra + " ";
	        				salaAFechar = salaAFechar.substring(11, salaAFechar.length()-1);
	        				salas.fechaSala(salaAFechar);
	        			}
	        			catch(Exception e) {System.out.println("Nao foi possivel fechar esta sala");}
	        			break;
	        		case "help":
	        			try
	        			{
	        				System.out.println("shutdown -> desliga o servidor");
	        				System.out.println("usuarios [nome sala] -> escreve os usuarios desta sala");
	        				System.out.println("salas -> escreve as salas criadas");
	        				System.out.println("criarsala [nome sala] -> cria uma sala");
	        				System.out.println("fecharsala [nome sala] -> cria uma sala");
	        				System.out.println("help -> exibe a ajuda");
	        			}
	        			catch(Exception e) {System.out.println("Algum problema aconteceu");}
	        			break;
	        		default: System.out.println("Comando nao conhecido, tente novamente");
	        	}
	        }
    	}
	    catch (IOException e)
	    {
	    	System.out.println(e);
	    }
    }
}