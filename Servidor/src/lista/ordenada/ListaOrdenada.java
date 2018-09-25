package lista.ordenada;

import java.lang.reflect.*;
import java.util.Vector;

public class ListaOrdenada <X extends Comparable<X>> implements Cloneable
{
	protected class No implements Cloneable
	{
		protected X info;
		protected No prox;
        protected No ant;

	//-----------------------------------

		public No (No a, X x, No n){
            this.ant  = a;
			this.info = x;
			this.prox = n;
		}

		public No (X x) {
			this (null, x, null);
		}

	//-----------------------------------

		public X getInfo () {
			return info;
		}

		public No getProx () {
			return prox;
		}
        
        public No getAnt () {
            return ant;
        }

		public void setInfo (X x) {
			info = x;
		}

		public void setProx (No no) {
			prox = no;
		}
        
        public void setAnt (No no){
            ant = no;
        }

	//-----------------------------------



	}//Fim da classe No

	protected No prim;
    protected No ultimo;
    protected No selecionado;

	public ListaOrdenada ()
	{
		prim = null;
        ultimo = null; 
        selecionado = null;
	}
    
    public ListaOrdenada (ListaOrdenada<X> l) throws Exception
    {
        if (l == null)
            throw new Exception ("Lista Nula");
            
        No aux = l.prim;        
        if (aux != null)//Checa se a lista a ser clonada possui No, se nao, acaba
        {
            this.prim = new No (aux.getInfo());            
            
            aux = aux.getProx();
            No atu = this.prim;
            while (aux != null) //Checa se a lista a ser clonada ainda possui mais elementos
            {                
               atu.setProx(new No(atu, aux.getInfo(), null));
               atu = atu.getProx();
               aux = aux.getProx();                          
            }
            ultimo = atu;
        }
        else //Se o primeiro elemento for nulo
        {
            this.prim = aux;
            ultimo = aux;
        }
              
    }    
    
    public X getItem ()
    {
        X ret = selecionado.getInfo();
        
        //Vai gerar um clone se for preciso
        if(ret instanceof Cloneable)
            ret = meuCloneDeX(ret);       
                    
        return ret;
    }
    
    public X getItem (X x)
    {
        X ret = null;
        No aux = selecionado;
        selecionado = prim;
        
        while(selecionado != null && ret == null)
        {
            if(selecionado.getInfo().equals(x))
                ret = this.getItem();
            this.lerProx();
        }
        
        selecionado = aux;
        return ret;
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
        		
    public void insira (X x) throws Exception
    {
    	X info;
    	
    	if (x == null)
    		throw new Exception ("ERRO ListaOrdenada: Objeto a ser inserido nulo");
    	
    	if(x instanceof Cloneable)
    		info = meuCloneDeX(x);    	
    	else
    		info = x;
    	
    	if(this.prim == null)
    	{
    		this.prim = new No(info);
    		this.ultimo = prim;
    		return;
    	}
    	
    	
    	No aux = this.prim;
    	boolean acabou = false;
    	
    	while (aux!=null)
    	{
	    	if (info.compareTo(aux.getInfo())<0)
	    	{
	    		acabou = true;
	    		if (aux==this.prim)
	    		{
	    			this.prim.setAnt(new No(null, info, this.prim));
	    			this.prim = this.prim.getAnt();
	    			break;
	    		}
	    		
	    		No antAntigo = aux.getAnt();
	    		antAntigo.setProx(new No (info));
	    		antAntigo.getProx().setAnt(antAntigo);
	    		antAntigo.getProx().setProx(aux);
	    		aux.setAnt(antAntigo.getProx());
	    		
	    		acabou = true;
	    		break;
	    	}
	    	aux = aux.getProx();
    	}
    	
    	if (!acabou)
    	{
    		this.ultimo = new No(ultimo, info, null);
    		this.ultimo.getAnt().setProx(this.ultimo);
    	}
    }
    
    public void menos (X x)
    // se nao houver o x a ser deletado na lista, o metodo nao faz nada
    {
    	if (x==null)
    		throw new IllegalArgumentException("ERRO ListaOrdenada: Valor a ser deletado eh nulo");
    	
    	if (this.prim.getInfo().compareTo(x)==0)
    	{
    		this.prim=this.prim.getProx();
    		this.prim.setAnt(null);
    		return;
    	}
    	
    	No aux = this.prim;
    	while (aux.getProx()!=null && aux.getProx().getInfo().compareTo(x)!=0)
    		aux = aux.getProx();
    	
    	if (aux.getProx()==null)
    		return;
    	
    	if (aux.getProx()==this.ultimo)
    	{
    		this.ultimo=aux;
    		aux.setProx(null);
    		return;
    	}
    	
    	aux.setProx(aux.getProx().getProx());
    	aux.getProx().setAnt(aux);
    }
    
    public void jogueForaPrimeiro () throws Exception
    {
    	if (selecionado == prim)
    		selecionado = null;
        if (this.prim == null)
            throw new Exception ("Lista Vazia");
        
        prim = this.prim.getProx();
        prim.setAnt(null);
    }
    
    public void jogueForaUltimo () throws Exception
    {
    	if (selecionado == ultimo)
    		selecionado = null;
        if (this.prim == null)
            throw new Exception ("Lista Vazia");
            
        if (ultimo.getAnt() != null)//Tem mais de um item na lista
        {
            ultimo = ultimo.getAnt();
            ultimo.setProx(null);
        }
        else // So tem um item;
        {
            ultimo = null;
            prim = null;
        }
    }

    public void reiniciarSelecionado ()
    {
    	this.selecionado = null;
    }
    
	@SuppressWarnings("unchecked")
	public X meuCloneDeX (X x)
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

	public Object[] toArray ()
	{
		Vector<X> vetor = new Vector<X>();
		
		No aux = this.prim;
		while (aux!=null)
		{
			vetor.add(aux.getInfo());
			aux = aux.getProx();
		}
		
		return vetor.toArray();
	}
	
	public String toString ()
	{
		String ret = "{";

		No atual = this.prim;
		while (atual != null)
		{
			ret += atual.getInfo();
			if (atual.getProx() != null) //se atual n eh o ultimo
				ret += ", ";				

			atual = atual.getProx();
		}

		ret += "}";
		return ret;
	}

	public boolean equals (Object obj)
	{
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if(!this.getClass().equals(obj.getClass()))
			return false;

		@SuppressWarnings("unchecked")
		ListaOrdenada<X> mod = (ListaOrdenada<X>)obj;		

		No aux1 = mod.prim;
		No aux2 = this.prim;
		while (aux1 != null && aux2 != null)
		{
			if (!aux1.equals(aux2))
				return false;

			aux1 = aux1.getProx();
			aux2 = aux2.getProx();
		}

		return true;
	}

	public int hashCode ()
	{
		int ret = 24;

		No aux = this.prim;
		while(aux != null)
			ret = ret * 2 + aux.hashCode();

		return ret;
	}

	public Object clone ()
	{
		ListaOrdenada<X> ret = null;
		try
		{
			ret = new ListaOrdenada<X> (this);
		}
		catch (Exception e) {}

		return ret;
	}

}
