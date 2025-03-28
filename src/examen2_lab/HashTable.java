package examen2_lab;

public class HashTable {
    private Entry lista;
    
    public void add(String username, long posicion) {
        Entry elemento = new Entry(username, posicion);
        if (lista == null) {
            lista = elemento;
        } else {
            Entry actual = lista;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(elemento);
        }
    }
    
    public void remove(String username) {
        if (lista == null) return;
        
        if (lista.getUsername().equals(username)) {
            lista = lista.getSiguiente();
            return;
        }
        
        Entry actual = lista;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getUsername().equals(username)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                return;
            }
            actual = actual.getSiguiente();
        }
    }
    
    public long search(String username) {
        Entry actual = lista;
        while (actual != null) {
            if (actual.getUsername().equals(username)) {
                return actual.getPosicion();
            }
            actual = actual.getSiguiente();
        }
        return -1;
    }
}