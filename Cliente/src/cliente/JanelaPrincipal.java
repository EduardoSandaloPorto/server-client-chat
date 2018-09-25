package cliente;

import comando.Comando;
//import lista.desordenada.*;
//import lista.ordenada.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;

public class JanelaPrincipal {

	private JFrame frmChat;
	private JTextField txtEnviar;
	private JTextArea txtAreaMensagens;
	@SuppressWarnings("rawtypes")
	private JList listaUsuarios;
	private boolean conectado=false;
	private Socket conexao;
	private ObjectInputStream receptor;
	private ObjectOutputStream transmissor;
	private DefaultListModel<String> model;
	private JLabel lblConectado;
	private JButton btnConectar;
	private JButton btnEnviar;
	private JScrollPane scrollPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JanelaPrincipal window = new JanelaPrincipal();
					window.frmChat.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JanelaPrincipal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		frmChat = new JFrame();
		frmChat.setResizable(false);
		frmChat.setTitle("Chat");
		frmChat.setBounds(50, 50, 857, 599);
		frmChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChat.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 96, 639, 389);
		frmChat.getContentPane().add(scrollPane);
		
		txtAreaMensagens = new JTextArea();
		txtAreaMensagens.setEditable(false);
		scrollPane.setViewportView(txtAreaMensagens);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(659, 96, 161, 425);
		frmChat.getContentPane().add(scrollPane_1);
		
		// Lista de usuarios
		model = new DefaultListModel<String>();
		listaUsuarios = new JList(model);
		scrollPane_1.setViewportView(listaUsuarios);
		
		// Area para escrever a mensagem que vai ser enviada
		txtEnviar = new JTextField();
		txtEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnEnviar.doClick();
			}
		});
		txtEnviar.setBounds(10, 496, 528, 25);
		frmChat.getContentPane().add(txtEnviar);
		txtEnviar.setColumns(10);
		
		// Botao para enviar mensagem
		btnEnviar = new JButton("Enviar");
		btnEnviar.setEnabled(false);
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				if (txtEnviar.getText().trim().equals(""))
					return;
				
				enviarMensagem(txtEnviar.getText(), "TODOS");
				
				txtEnviar.setText("");
			}
		});
		
		btnEnviar.setBounds(544, 496, 105, 25);
		frmChat.getContentPane().add(btnEnviar);
		
		
		
		JLabel lblNewLabel = new JLabel("Usu\u00E1rios");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(659, 73, 105, 25);
		frmChat.getContentPane().add(lblNewLabel);
		
		JLabel lblMensagens = new JLabel("Mensagens");
		lblMensagens.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMensagens.setBounds(10, 73, 105, 25);
		frmChat.getContentPane().add(lblMensagens);
		
		lblConectado = new JLabel("Voc\u00EA n\u00E3o est\u00E1 conectado a nenhuma sala!");
		lblConectado.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblConectado.setBounds(10, 21, 639, 35);
		frmChat.getContentPane().add(lblConectado);
		
		
		// Botao para conectar a um servidor
		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				conectarOuDesconectar();
			}
		});
		btnConectar.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnConectar.setBounds(659, 21, 161, 32);
		frmChat.getContentPane().add(btnConectar);
		
		JLabel lblNewLabel_1 = new JLabel("Clique duas vezes no nome de algu\u00E9m para enviar uma mensagem particular");
		lblNewLabel_1.setBounds(356, 532, 464, 18);
		frmChat.getContentPane().add(lblNewLabel_1);
		
		
		// USUARIO FECHOU O PROGRAMA - FECHAR CONEXAO
		frmChat.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e)
            {
            	try
            	{
            		transmissor.close();
					transmissor = null;
					receptor.close();
					receptor = null;
					conexao.close();
					conexao = null;
					return;
            	}
            	catch (Exception erro) {}
            }
        });
		
		// Evento double click da lista de usuarios
		listaUsuarios.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt)
		    {
		    	if (!conectado)
		    		return;
		    	
				JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2)
		        {
		            // Double-click detectado
		            int index = list.locationToIndex(evt.getPoint());
		            
		            list.setSelectedIndex(index);
		            String usuarioAReceber = (String)list.getSelectedValue();
		            
		            String mensagemAEnviar = JOptionPane.showInputDialog("Digite a mensagem particular a ser enviada para " + usuarioAReceber);
		            enviarMensagem(mensagemAEnviar, usuarioAReceber);
		        }
		    }
		});
		
		
	}
	
	
	private void conectarOuDesconectar()
	{
		if (!conectado)
		// PARTE 1 DA CONEXAO - VALIDACAO DE IP E PORTA
		{
			String ipAConectar;
			int porta=0;
			String nomeSala;
			
			for (;;)
			{
				ipAConectar = JOptionPane.showInputDialog("Por favor, digite o IP do servidor que deseja se conectar.");
				
				if (ipAConectar == null)
					return;
				
				if (!ipEhValido(ipAConectar))
				{
					JOptionPane.showMessageDialog(null, "Digite um IP válido.");
					continue;
				}
				
				break;
			}
			
			for (;;)
			{
				String resp=null;
				try
				{
					resp = JOptionPane.showInputDialog("Por favor, digite a porta que deseja se conectar. (Padrao: 17170)");
					porta = Integer.parseInt(resp);
				}
				catch (Exception e)
				{
					if (resp==null)
						return;
					
					JOptionPane.showMessageDialog(null, "Digite uma porta válida.");
					continue;
				}
				
				if (!portaEhValida(porta))
				{
					JOptionPane.showMessageDialog(null, "Digite uma porta válida.");
					continue;
				}
			
				break;
			}
			
			
			// PARTE 2 DA CONEXAO - CONECTANDO AO SERVIDOR
			try
			{
				this.conexao = new Socket(ipAConectar, porta);
				this.transmissor = new ObjectOutputStream(conexao.getOutputStream());
				transmissor.flush();
				this.receptor = new ObjectInputStream(conexao.getInputStream());
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Houve algum erro ao conectar com o servidor.");
				return;
			}
			
			Comando resposta;
			// PARTE 3 DA CONEXAO - ENVIANDO DADOS DO USUARIO
			try
			{
				// PASSO I) ENVIAR A SALA QUE O USUARIO QUER CONECTAR
				
				resposta = (Comando)this.receptor.readObject();
				String[] nomSls = (String[])((Comando)this.receptor.readObject()).getMensagem();
				
				String salaSelecionada = (String)JOptionPane.showInputDialog(null, (String)resposta.getMensagem(), "Seleção de salas", JOptionPane.QUESTION_MESSAGE, null, nomSls, "Regular");
				nomeSala = salaSelecionada;
				
				if (salaSelecionada==null)
				{
					transmissor.close();
					transmissor = null;
					receptor.close();
					receptor = null;
					conexao.close();
					conexao = null;
					return;
				}
				
				int indexSala = 0;
				for (String nome : nomSls)
				{
					if (nome.equals(salaSelecionada))
						break;
					indexSala++;
				}
				
				// Para isso ocorrer, o usuario alterou algo nos arquivos do cliente indevidamente
				if (indexSala==nomSls.length)
				{
					JOptionPane.showMessageDialog(null, "Ocorreu um erro ao entrar na sala. A conexão foi fechada.");
					transmissor.close();
					transmissor = null;
					receptor.close();
					receptor = null;
					conexao.close();
					conexao = null;
					return;
				}
				
				transmissor.writeObject(new Comando("[MENSAGEM]", "Servidor", "Usuario", new Integer (indexSala)));
				transmissor.flush();
				
				
				// PASSO II) ENVIAR O NOME DE USUARIO
				
				resposta = (Comando)this.receptor.readObject();
				
				for (;;)
				{
					String nomeUsuario = JOptionPane.showInputDialog(resposta.getMensagem());
					
					if (nomeUsuario==null)
					{
						transmissor.close();
						transmissor = null;
						receptor.close();
						receptor = null;
						conexao.close();
						conexao = null;
						return;
					}
					
					transmissor.writeObject(new Comando("[ENTRAR]", "Servidor", "Usuario", nomeUsuario));
					transmissor.flush();
					
					resposta = (Comando)this.receptor.readObject();
					
					if (resposta.getProtocolo().equals("[ERRO]"))
						continue;
					
					if (resposta.getProtocolo().equals("[SUCESSO]"))
						break;
				}
				
				
				// PASSO III) VERIFICAR SE O NOME DE USUARIO JA EXISTE
				
				resposta = (Comando)this.receptor.readObject();
				
				for (;;)
				{
					if (resposta.getProtocolo().equals("[SUCESSO]")) // o servidor aceitou o nome de usuario
						break;
					
					String nomeUsuario = JOptionPane.showInputDialog(resposta.getMensagem());
					
					if (nomeUsuario==null)
					{
						transmissor.close();
						transmissor = null;
						receptor.close();
						receptor = null;
						conexao.close();
						conexao = null;
						return;
					}
					
					transmissor.writeObject(new Comando("[ENTRAR]", "Servidor", "Usuario", nomeUsuario));
					transmissor.flush();
					
					resposta = (Comando)this.receptor.readObject();
				}
				
				
				// PASSO IV) ESCREVER OS USUÁRIOS DA SALA NA TELA
				
				resposta = (Comando)this.receptor.readObject();
				Object[] usuarios = (Object[])resposta.getMensagem();
				
				for (Object nomeDeUmUsuario : usuarios)
					this.model.addElement(nomeDeUmUsuario.toString());
				
				
				// SE O PROGRAMA CHEGOU ATÉ AQUI, O USUÁRIO ESTÁ CONECTADO CORRETAMENTE.
				txtAreaMensagens.setText("");
				this.conectado = true;
				this.lblConectado.setText("Conectado à sala: " + nomeSala);
				btnConectar.setText("Desconectar");
				btnEnviar.setEnabled(true);
				
				receberMensagens();
				
				return;
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "A conexão com o servidor foi perdida.");
				this.transmissor = null;
				this.receptor = null;
				this.conexao = null;
			}
		}
		
		// Usuário já está conectado
		try
		{
			transmissor.close();
			transmissor = null;
			receptor.close();
			receptor = null;
			conexao.close();
			conexao = null;
			
			this.lblConectado.setText("Voc\u00EA n\u00E3o est\u00E1 conectado a nenhuma sala!");
			this.btnConectar.setText("Conectar");
			model.removeAllElements();
			btnEnviar.setEnabled(false);
			
			
			this.conectado = false;
		}
		catch (Exception e) {}
	}
	
	private void enviarMensagem (String mensagem, String destinatario)
	{
		if (!conectado)
			return;
		
		try
		{
			transmissor.writeObject(new Comando("[MENSAGEM]", destinatario, "Usuario", mensagem));
			transmissor.flush();
		}
		catch (Exception e) {}
	}
	
	private void receberMensagens ()
	{
		// Criacao de thread para escrever as mensagens recebidas
		Thread receber = new Thread()
		{
			public void run()
			{
				try
				{
					while (conectado)
					{
						Comando c = (Comando)receptor.readObject();
						tratarComando(c);
					}
				}
				catch (Exception e)
				{
					transmissor = null;
					receptor = null;
					conexao = null;
					
					conectado = false;
					
					lblConectado.setText("Voc\u00EA n\u00E3o est\u00E1 conectado a nenhuma sala!");
					btnConectar.setText("Conectar");
					model.removeAllElements();
					btnEnviar.setEnabled(false);
				}
			}
		};
		
		receber.start();
	}
	
	private void tratarComando (Comando c)
	{
		if (c.getProtocolo().equals("[MENSAGEM]"))
		{
			txtAreaMensagens.append((String)c.getMensagem()+"\n");
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue( vertical.getMaximum() );
			return;
		}
		
		if (c.getProtocolo().equals("[NICK-IN]"))
		{
			model.addElement((String)c.getMensagem());
			return;
		}
		
		if (c.getProtocolo().equals("[NICK-OUT]"))
		{
			model.removeElement((String)c.getMensagem());
			return;
		}
	}
	
	
	
	public static boolean ipEhValido(String text)
	{
		if (text.equals("localhost"))
			return true;
		
	    Pattern p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	    Matcher m = p.matcher(text);
	    return m.find();
	}
	
	public static boolean portaEhValida(int porta)
	{
		if (porta < 0 || porta > 65535)
			return false;
		
		return true;
	}
}
