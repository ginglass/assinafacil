package net.sf.assinafacil;

/** 
* Uma classe que encapsula pares de objetos. 
* Inspirado no template pair<> da STL. 
* 
* Dica do forum interessante (author thingol) 
* 
* @param <T> O primeiro tipo. 
* @param <U> O segundo tipo.
*/  
public class Pair<T,U> {
    /** O primeiro objeto. */  
    public T first;  
  
    /** O segundo objeto. */  
    public U second;  
  
    /** Construtor */  
    public Pair(T t, U u) {  
        first = t;  
        second = u;  
    }  
}
