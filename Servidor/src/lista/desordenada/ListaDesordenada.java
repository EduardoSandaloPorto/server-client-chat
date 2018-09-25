package lista.desordenada;

import java.lang.reflect.*;

public class ListaDesordenada<X>
{
	public ListaDesordenada () 
	{
		this.prim = null;
		this.penultimo = null;
		this.selecionado = null;
	}

	protected class No
	{
		protected X  info;
		protected No prox;

		public X  getInfo () { return this.info; }
		public No getProx () { return this.prox; }

		public void setInfo (X x)
		{
			this.info = x;
		}

		public void setProx ( No n)
		{
			this.prox = n;
		}

		public No (X x, No n)
		{
			this.info=x;
			this.prox=n;
		}

		public No (X x)
		{
			this (x, null);
		}
	}

	@SuppressWarnings("unchecked")
	protected X meuCloneDeX (X x)
    {
        X ret = null;

        try
        {
            Class<?> classe = x.getClass();
            Class<?>[] tipoDoParametroFormal = null; // pq clone tem 0 parametros
            Method metodo = classe.getMethod ("clone", tipoDoParametroFormal);
            Object[] parametroReal = null;// pq clone tem 0 parametros
            ret = ((X)metodo.invoke (x, parametroReal));
        }
        catch (NoSuchMethodException erro)
        {}
        catch (InvocationTargetException erro)
        {}
        catch (IllegalAccessException erro)
        {}

        return ret;
    }

	protected No prim;
	protected No penultimo;
	protected No selecionado;

	public X getItem ()
    {
        X ret = selecionado.getInfo();
        
        //PARA ESTE PROGRAMA, NAO QUEREMOS CLONE DE X.
        
        //Vai gerar um clone se for preciso
        //if(ret instanceof Cloneable)
        //    ret = meuCloneDeX(ret);
        
        return ret;
    }
	
	public void reiniciarSelecionado ()
    {
    	this.selecionado = null;
    }
	
	public boolean lerProx()
    {                   
    	if(selecionado == null)
    		selecionado = prim;
    	else
    		selecionado = selecionado.getProx();
    	
    	if (selecionado != null)
    		return true;    
    		
                	        	        
        return false;
    }
	
	public void removerSelecionado ()
	{
		if (selecionado==null)
			return;
		
		if (selecionado==prim)
			try
			{
				this.jogueForaPrimeiro();
				return;
			}
			catch (Exception e) {}
		
		No aux = prim;
		while (aux.getProx()!=selecionado)
		{
			aux = aux.getProx();
		}
		
		if (aux==penultimo)
		{
			try
			{
				jogueForaUltimo();
				return;
			}
			catch (Exception e) {}
		}
		
		if (aux.getProx()==penultimo)
		{
			penultimo = aux;
			aux.setProx(aux.getProx().getProx());
			return;
		}
		
		aux.setProx(aux.getProx().getProx());
	}
	
	public X getInfoPrim() throws Exception
	{ 
		if (this.prim == null)
			throw new Exception ("A lista esta vazia");
		return this.prim.getInfo();
	}

	public X getInfoPenultimo() throws Exception
	{
		if (this.penultimo == null)
			throw new Exception ("A lista nao possui penultimo item");
		return this.penultimo.getInfo();
	}

	public void insiraNoInicio (X x) throws Exception
	{
		if (x==null)
			throw new Exception ("Informacao ausente");

		X info;

		if (x instanceof Cloneable)
			info = meuCloneDeX(x);
		else
			info = x;

		if (this.prim==null)
		{
			this.prim = new No (info, null);
			return;
		}
		
		this.prim = new No (info,this.prim);

		if (prim.getProx()!=null)
            if (this.prim.getProx().getProx()==null)
                this.penultimo = this.prim;
	}

	public void insiraNoFim (X x) throws Exception
	{
		if (x==null)
			throw new Exception("Informacao ausente");

		X info;

		if (x instanceof Cloneable)
			info = meuCloneDeX(x);
		else
			info = x;

		if (this.penultimo!=null)
		{
			this.penultimo = this.penultimo.getProx();
			this.penultimo.setProx(new No (info));
			return;
		}

		if (this.prim==null)
			this.prim = new No (info);
		else
		{
			No atu=this.prim;
			while (atu.getProx()!=null)
				atu=atu.getProx();
			this.penultimo = atu;
			atu.setProx (new No (info));
		}
	}

	public void jogueForaPrimeiro() throws Exception
	{
		if (selecionado==prim)
			selecionado = null;
		
		if (this.prim==null)
			throw new Exception ("Nao foi possivel remover: A lista esta vazia.");

		this.prim = this.prim.getProx();
		
		if (this.prim==null)
			this.penultimo=null;
		else
		if (this.prim.getProx()==null)
			this.penultimo=null;
	}

	public void jogueForaUltimo() throws Exception
	{
		if (selecionado == this.penultimo.getProx())
			this.selecionado = null;
		
		if (this.prim == null)
			throw new Exception ("Nao foi possivel remover: A lista esta vazia.");

		if (this.prim.getProx()==null)
		{
			this.prim=null;
			return;
		}

		No atual = this.prim;
		No atualPenultimo = this.prim;

		while (atual.getProx().getProx()!=null)
		{
			atualPenultimo = atual;
			atual = atual.getProx();
		}
		atual.setProx(null);
		this.penultimo = atualPenultimo;

		/*
			em linguagem sem Garbage Collector:
			lixo = atual.getProx();
			atual.setProx(null);
			free(lixo);
		*/
	}

	public String toString()
	{
		String ret   = "{";
		No     atual = this.prim;

		while (atual!=null)
		{
			ret = ret+atual.getInfo();

			if (atual.getProx()!=null) // se atual nao é o ultimo
				ret=ret+", ";

			atual = atual.getProx();

		}

		ret += "}";

		return ret;
	}
	
	public int hashCode ()
	{
		int ret=2;

		No atual = this.prim;

		while (atual!=null)
		{
			ret   = ret * 3 + atual.getInfo().hashCode();
			atual = atual.getProx();
		}

		return ret;
	}

	public boolean equals (Object obj)
	{
		if (obj==null)
			return false;
		if (obj==this)
			return true;
		if (!(this.getClass().equals(obj.getClass())))
			return false;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ListaDesordenada<X> lis = (ListaDesordenada)obj;

		No atual  = this.prim;
		No atual2 = lis.prim;

		while (atual!=null)
		{
			if (atual2==null)
				return false;
			if (!(atual.getInfo().equals(atual2.getInfo())))
				return false;
			atual = atual.getProx();
			atual2 = atual2.getProx();
		}

		if (atual2!=null)
			return false;

		return true;

	}

	public ListaDesordenada (ListaDesordenada<X> modelo) throws Exception
	{
		if (modelo == null)
			throw new Exception ("Modelo ausente");

		if (modelo.prim==null)
			return;

		No atualModelo = modelo.prim;
		No atualThis;

		this.prim   = new No(atualModelo.getInfo());
		atualModelo = atualModelo.getProx();
		atualThis   = this.prim;

		while (atualModelo!=null)
		{
			atualThis.setProx(new No(atualModelo.getInfo()));
			atualThis   = atualThis.getProx();
			atualModelo = atualModelo.getProx();
		}

	} 

	public Object clone ()
	{
		ListaDesordenada<X> ret = new ListaDesordenada<X> ();

		/*
		if (this.prim==null)
			return ret;

		ret.prim = new No (this.prim.getInfo());

		No atual = this.prim.getProx();
		No aux   = ret.prim;

		while (atual != null)
		{
			aux.setProx(new No(atual.getInfo()));

			atual = atual.getProx();
			aux   = aux.getProx();
		}
		return ret;
		*/
		
			try
			{
				ret = new ListaDesordenada<X> (this);
			}
			catch (Exception erro)
			{}

			return ret;
	
	}
}
