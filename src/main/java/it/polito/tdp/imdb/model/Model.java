package it.polito.tdp.imdb.model;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;
import it.polito.tdp.imdb.model.Event.EventType;

public class Model {
	
	private List<String> genres;
	private ImdbDAO dao = new ImdbDAO();
	private SimpleWeightedGraph<Actor, DefaultWeightedEdge> grafo;
	private Random r = new Random();
	
	
	
	private ArrayList<Actor> attoriNelGrafo;
	private int numDays;
	private PriorityQueue<Event> queue;
	private ArrayList<Actor> actorsCasted;
	
	
	public Model() {
		this.genres = dao.listAllGenres(); 
	}
	public List<String> getGenres() {
		return genres;
	}
	
	public void creaGrafo(String genre) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		List<Actor> actors = this.dao.listAllActorsGenre(genre);
		for (Actor actor: actors) {
			this.grafo.addVertex(actor);
		}
		List<Actor[]> collabs = this.dao.listAllActorsCollabGenre(genre);
		for (Actor[] collab: collabs) {
			DefaultWeightedEdge edge = grafo.getEdge(collab[0], collab[1]);
			if (grafo.getEdge(collab[0], collab[1]) != null) {
				grafo.setEdgeWeight(edge, grafo.getEdgeWeight(edge)+1);
			} else {
				edge = grafo.addEdge(collab[0], collab[1]);
				grafo.setEdgeWeight(edge, 1);
			}
		}
	}
	
	public Set<Actor> attoriNelGrafo(){
		return new TreeSet<Actor>(this.grafo.vertexSet());
	}
	
	public Set<Actor> getAttoriSimili(Actor a1){
		BreadthFirstIterator<Actor, DefaultWeightedEdge> iterator = new BreadthFirstIterator<>(this.grafo, a1);
	
		Set<Actor> visitati = new TreeSet<Actor>();
		
		while (iterator.hasNext()) {
			Actor found = iterator.next();
			if (!visitati.contains(found) && !found.equals(a1)) {
				visitati.add(found);
			} else {
				//skip
			}
		}
		
		return visitati;
	}
	
	public void setSimulation(int numDays) {
		this.actorsCasted = new ArrayList<Actor>();
		this.numDays = numDays;
		this.queue = new PriorityQueue<Event>();
		this.attoriNelGrafo = new ArrayList<Actor>(this.grafo.vertexSet());
		
		this.queue.add(new Event(0, EventType.SCELTA_CASUALE));
	}
	
	public void runSimulation() {
		while(!this.queue.isEmpty() && this.queue.peek().getGiorno()<this.numDays) {
			this.processEvent(this.queue.poll());
		}
	}
	
	private void processEvent(Event e) {
		switch (e.getTipo()) {
		case SCELTA_CASUALE:
			boolean valid = false;
			while (!valid) {
				int index = r.nextInt(this.grafo.vertexSet().size());
				Actor chosen = attoriNelGrafo.get(index);
				if (!actorsCasted.contains(chosen)) {
					actorsCasted.add(chosen);
					valid = true;
				}
			}
			this.queue.add(new Event(e.getGiorno() + 1, EventType.GIORNO_NORMALE));
			break;
		case PAUSA:
			this.actorsCasted.add(null);
			this.queue.add(new Event(e.getGiorno() + 1, EventType.SCELTA_CASUALE));
			break;
		case GIORNO_NORMALE:
			double random = r.nextDouble();
			if (random < 0.6) {
				this.queue.add(new Event(e.getGiorno(), EventType.SCELTA_CASUALE));
			} else {
				Actor last = this.actorsCasted.get(this.actorsCasted.size() - 1);
				Set<DefaultWeightedEdge> outgoingEdges = this.grafo.outgoingEdgesOf(last);
				double max = 0;
				if (outgoingEdges.size() > 0) {
					for (DefaultWeightedEdge edge: outgoingEdges) {
						if (max < this.grafo.getEdgeWeight(edge)) {
							max = this.grafo.getEdgeWeight(edge);
						}
					}
					
					List<DefaultWeightedEdge> edgesMax = new ArrayList<DefaultWeightedEdge>();
					
					for (DefaultWeightedEdge edge: this.grafo.outgoingEdgesOf(last)) {
						if (max == this.grafo.getEdgeWeight(edge)) {
							edgesMax.add(edge);
						}
					}
					
					Collections.shuffle(edgesMax);
					boolean valid2 = false;
					for (DefaultWeightedEdge edge: edgesMax) {
						Actor chosen2;
						if (this.grafo.getEdgeSource(edge).equals(last)) {
							chosen2 = this.grafo.getEdgeTarget(edge);
						} else {
							chosen2 = this.grafo.getEdgeSource(edge);
						}
						if (!actorsCasted.contains(chosen2)) {
							actorsCasted.add(chosen2);
							valid2 = true;
							this.queue.add(new Event(e.getGiorno(), EventType.GIORNO_NORMALE));
							break;
						}
					}
					
					if (!valid2) {
						this.queue.add(new Event(e.getGiorno(), EventType.SCELTA_CASUALE));
					}
				} else {
					this.queue.add(new Event(e.getGiorno(), EventType.SCELTA_CASUALE));
				}
			}
			break;
		}
		int numAttori = this.actorsCasted.size();
		if (numAttori > 1) {
			Actor a1 = this.actorsCasted.get(numAttori - 1);
			Actor a2 = this.actorsCasted.get(numAttori - 2);
			if (a1 != null && a2 != null) {
				if (a1.getGender().contentEquals(a2.getGender())) {
					this.queue.poll();
					this.queue.add(new Event(e.getGiorno() + 1, EventType.PAUSA));
				}
			}
		}
	}
	public ArrayList<Actor> getActorsCasted() {
		return actorsCasted;
	}
}
