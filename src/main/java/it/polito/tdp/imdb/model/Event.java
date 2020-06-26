package it.polito.tdp.imdb.model;

public class Event implements Comparable<Event> {
	public enum EventType{
		SCELTA_CASUALE,
		PAUSA,
		GIORNO_NORMALE
	}
	
	int giorno;
	EventType tipo;
	
	public Event(int giorno, EventType tipo) {
		super();
		this.giorno = giorno;
		this.tipo = tipo;
	}

	public int getGiorno() {
		return giorno;
	}

	public EventType getTipo() {
		return tipo;
	}

	@Override
	public int compareTo(Event o) {
		return Integer.compare(this.giorno, o.giorno);
	}
	
}
