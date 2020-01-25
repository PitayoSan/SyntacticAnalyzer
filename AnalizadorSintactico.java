package Practica2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class AnalizadorSintactico {

	private String cadena;
	private Celda[][] tablaCYK;
	private String productor;
	private Hashtable<String,List<String>> producciones;
	private Hashtable<String,List<String>> generadores;

	
	public void ingresarDatos(String[] input) {
		System.out.println("-----Analizador Sintáctico-----");
		System.out.println();
		if(input.length > 1) {
			System.out.println("Ingresando datos:");
			System.out.println();
			//Inicializar atributos
			this.cadena = this.productor = "";
			this.producciones = new Hashtable<>();
			this.generadores = new Hashtable<>();
			this.cadena = input[0];
			
			System.out.println("Cadena ingresada: " + cadena);
		
			for(int elemento = 1; elemento < input.length; elemento++) {
				Scanner sc = new Scanner(input[elemento]);
				String[] separador = sc.nextLine().split("\\|");
				if(input[elemento].equals("") || separador.length < 2) {
					System.out.println();
					System.out.println("Los generadores deben ser no vacios, contar con el\n"
									 + "formato correcto y contener 2 o más valores. Inténtelo de nuevo");
					System.exit(1);
				}
				String nombre = separador[0];
				System.out.print(nombre + "->");
				List<String> newGen = new ArrayList<String>();
				for(int elem = 1; elem < separador.length; elem++) {
					if((separador[elem].length() == 1 && Character.isUpperCase(separador[elem].charAt(0)))
					   || (separador[elem].length() == 2 &&
					   (!Character.isUpperCase(separador[elem].charAt(0)) || !Character.isUpperCase(separador[elem].charAt(1))))
					   || (separador[elem].length() < 1) || (separador[elem].length() > 2)) {
						System.out.println();
						System.out.println("Los símbolos terminales deben tener exactamente\n"
										 + "1 símbolo minúsculo (en caso de ser letra) y los\n"
										 + "generadores deben ser exactamente dos letras mayúsculas.\n"
										 + "Inténtelo de nuevo");
						System.exit(1);
					}
					List<String> newPro;
					if(this.producciones.containsKey(separador[elem])) {
						newPro = this.producciones.get(separador[elem]);
						newPro.add(nombre);
						this.producciones.put(separador[elem],newPro);
					} else {
						newPro = new ArrayList<String>();
						newPro.add(nombre);
						this.producciones.put(separador[elem], newPro);
					}
					newGen.add(separador[elem]);
					System.out.print(newGen.get(elem-1));
					if(elem < separador.length-1) {
						System.out.print("|");
					} else {
						System.out.println();
					}
				}
				this.generadores.put(nombre,newGen);
				sc.close();
			}

			this.productor = input[1].substring(0,1);
			/*
			System.out.println("Producciones: " + this.producciones.size());
			for(Map.Entry<String, List<String>> entry : this.producciones.entrySet()) {
				List<String> temp = entry.getValue();
				for(int j = 0; j < temp.size(); j++) {
					if(j < temp.size()-1) {
						System.out.print(temp.get(j) + ",");
					} else {
						System.out.print(temp.get(j) + "->");
					}
				}
				System.out.print(entry.getKey());
				System.out.println();
				
			}
			*/
			this.tablaCYK = new Celda[this.cadena.length()][this.cadena.length()];
		} else {
			System.out.println();
			System.out.println("El programa necesita de una cadena y por lo menos un generador\n"
							 + "para operar. Inténtelo de nuevo");
			System.exit(1);
		}
		System.out.println();
		System.out.println("Calculando tabla:");
		System.out.println();
		this.calcularTabla();
		System.out.println("Imprimiendo tabla:");
		System.out.println();
		this.imprimirTabla();
		//System.out.println("Imrpimiendo árbol:");
		//System.out.println();
		//this.imprimirArbol();
	}
	
	private void calcularTabla() {
		int largo = this.cadena.length();
		//Almacena las celdas vistas
		Hashtable<String,Celda> vistas = new Hashtable<>();
		//Tomar los generadores de los terminales
		for(int x = 0; x < largo; x++) {
			if(this.producciones.containsKey(this.cadena.substring(x,x+1))) {
				this.tablaCYK[x][largo-1] = new Celda();
				List<String> temp = this.producciones.get(this.cadena.substring(x,x+1));
				for(int e = 0; e < temp.size(); e++) {
					this.tablaCYK[x][largo-1].addElemento(temp.get(e));
				}
				this.tablaCYK[x][largo-1].setSubstring(this.cadena.substring(x,x+1));
				vistas.put(this.cadena.substring(x,x+1), this.tablaCYK[x][largo-1]);
			} else {
				System.out.println("La cadena no es aceptada");
				System.exit(1);
			}
		}
		//Obtener el substring del segun el nivel en adelante
		for(int y = largo-2; y >= 0; y--) {
			for(int x = 0; x <= y; x++) {
				//Obtiene el String con longitud en base al nivel en el que este
				String cadenaActual = this.cadena.substring(x,x+largo-y);
				//Nombra la celda con el substring
				this.tablaCYK[x][y] = new Celda();
				this.tablaCYK[x][y].setSubstring(cadenaActual);
				//Realiza las particiones debidas a ese String
				for(int subs = 1; subs < cadenaActual.length(); subs++) {
					String particion1 = cadenaActual.substring(0,subs);
					String particion2 = cadenaActual.substring(subs,cadenaActual.length());
					Celda celda1 = vistas.get(particion1);
					Celda celda2 = vistas.get(particion2);
					String combPosible = "";
					if(celda1.getSize() > celda2.getSize()) {
						for(int c2 = 0; c2 < celda2.getSize(); c2++) {
							for(int c1 = 0; c1 < celda1.getSize(); c1++) {
								combPosible = celda1.getElemento(c1) + celda2.getElemento(c2);
								if(this.producciones.containsKey(combPosible)) {
									List<String> gens = this.producciones.get(combPosible);
									for(int gen = 0; gen < gens.size(); gen++) {
										if(!this.tablaCYK[x][y].searchElemento(gens.get(gen))) {
											this.tablaCYK[x][y].addElemento(gens.get(gen));
										}
									}
								}
							}
						}
					} else {
						for(int c1 = 0; c1 < celda1.getSize(); c1++) {
							for(int c2 = 0; c2 < celda2.getSize(); c2++) {
								combPosible = celda1.getElemento(c1) + celda2.getElemento(c2);
								if(this.producciones.containsKey(combPosible)) {
									List<String> gens = this.producciones.get(combPosible);
									for(int gen = 0; gen < gens.size(); gen++) {
										if(!this.tablaCYK[x][y].searchElemento(gens.get(gen))) {
											this.tablaCYK[x][y].addElemento(gens.get(gen));
										}
									}
								}
							}
						}
					}
				}
				vistas.put(cadenaActual,this.tablaCYK[x][y]);
			}
		}
		
	}
	
	private void imprimirTabla() {
		int largo = this.cadena.length();
		for(int y = largo; y > 0 ; y--) {
			System.out.print(y + "	");
			for(int x = 0; x < largo+1-y; x++) {
				if(this.tablaCYK[x][largo-y].getSize() > 0) {
					for(int e = 0; e < this.tablaCYK[x][largo-y].getSize(); e++ ) {
						System.out.print(this.tablaCYK[x][largo-y].getElemento(e));
						if(e < this.tablaCYK[x][largo-y].getSize()-1) {
							System.out.print(",");
						}
					}
				} else {
					System.out.print("-");
				}
				System.out.print("	");
			}
			System.out.println();
		}
		System.out.print("	");
		for(int x = 1; x <= largo; x++) {
			System.out.print(x + "	");
		}
		System.out.println();
		System.out.print("	");
		for(int x = 1; x <= largo; x++) {
			System.out.print("-" + "	");
		}
		System.out.println();
		System.out.print("	");
		for(int x = 0; x < largo; x++) {
			System.out.print(this.cadena.substring(x,x+1) + "	");
		}
		System.out.println("\n");
		
		if(this.tablaCYK[0][0].getSize() > 0 && this.tablaCYK[0][0].searchElemento(this.productor)) { 
			System.out.println("La cadena es aceptada");
		} else {
			System.out.println("La cadena fue rechazada");
			System.exit(1);
		}
		System.out.println();
	}
	
	//Método incompleto
	private void encapsularArbol() {
		int largo = this.cadena.length();
		int nodos = 1;
		Nodo raiz = new Nodo();
		RAIZ:
		for(int genes = 0; genes < this.tablaCYK[0][0].getSize(); genes++) {
			raiz.setContenido(this.tablaCYK[0][0].getElemento(genes));
		}
	}

	//Método incompleto
	private void imprimirArbol() {
		Nodo raiz = new Nodo();
		Queue<Nodo> queue = new LinkedList();
		queue.add(raiz);
		int largo = this.cadena.length();
		while(!queue.isEmpty()) {
			Nodo nodoTmp = queue.poll();
			System.out.println(nodoTmp.getContenido());
			queue.add(nodoTmp.getIzquierdo());
			queue.add(nodoTmp.getDerecho());
			largo--;
		}
		System.out.println();
	}
	
	//Métodos para imprimir árbol (no utilizado)
	    private void imprimirNodo(Nodo raiz) {
	        int largo = this.largo(raiz);

	        imprimirIntervalo(Collections.singletonList(raiz), 1, largo);
	    }

	    private void imprimirIntervalo(List<Nodo> nodos, int nivel, int largo) {
	        if (nodos.isEmpty() || this.estaVacio(nodos))
	            return;

	        int piso = largo - nivel;
	        int bordes = (int) Math.pow(2, (Math.max(piso - 1, 0)));
	        int primeros = (int) Math.pow(2, (piso)) - 1;
	        int enmedio = (int) Math.pow(2, (piso + 1)) - 1;

	        this.espacios(primeros);

	        List<Nodo> nuevos = new ArrayList<Nodo>();
	        for (Nodo nodo : nodos) {
	            if (nodo != null) {
	                System.out.print(nodo.getContenido());
	                nuevos.add(nodo.getIzquierdo());
	                nuevos.add(nodo.getDerecho());
	            } else {
	            	nuevos.add(null);
	            	nuevos.add(null);
	                System.out.print(" ");
	            }

	            this.espacios(enmedio);
	        }
	        System.out.println("");

	        for (int i = 1; i <= bordes; i++) {
	            for (int j = 0; j < nodos.size(); j++) {
	                this.espacios(primeros - i);
	                if (nodos.get(j) == null) {
	                    this.espacios(bordes + bordes + i + 1);
	                    continue;
	                }

	                if (nodos.get(j).getIzquierdo() != null) {
	                    System.out.print("/");
	                } else {
	                    this.espacios(1);
	                }
	                this.espacios(i + i - 1);

	                if (nodos.get(j).getDerecho() != null) {
	                    System.out.print("\\");
	                } else {
	                    this.espacios(1);
	                }
	                this.espacios(bordes + bordes - i);
	            }

	            System.out.println("");
	        }

	        this.imprimirIntervalo(nuevos, nivel + 1, largo);
	    }

	    private void espacios(int contar) {
	        for (int index = 0; index < contar; index++)
	            System.out.print(" ");
	    }

	    private int largo(Nodo nodo) {
	        if (nodo == null)
	            return 0;

	        return Math.max(this.largo(nodo.getIzquierdo()), this.largo(nodo.getDerecho())) + 1;
	    }

	    private boolean estaVacio(List<Nodo> list) {
	        for (Nodo nodo : list) {
	            if (nodo != null)
	                return false;
	        }

	        return true;
	    }
	
	public static void main(String[] args) {
		AnalizadorSintactico as = new AnalizadorSintactico();
		as.ingresarDatos(args);
	}
}

class Nodo {
	private String contenido;
	private Nodo izquierdo,
				 derecho;
	
	public Nodo() {
		this("");
	}
	
	public Nodo(String contenido) {
		this.setContenido(contenido);
		this.izquierdo = this.derecho = null;
	}
	
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	
	public String getContenido() {
		return this.contenido;
	}
	
	public void setIzuierdo(Nodo izquierdo) {
		this.izquierdo = izquierdo;
	}
	
	public Nodo getIzquierdo() {
		return this.izquierdo;
	}
	
	public void setDerecho(Nodo derecho) {
		this.derecho = derecho;
	}
	
	public Nodo getDerecho() {
		return this.derecho;
	}
}
	
class Celda {
	private ArrayList<String> contenido;
	private String substring;
	
	public Celda() {
		this.contenido = new ArrayList<>();
		this.substring = "";
	}
	
	public Celda(String elemento) {
		this.contenido = new ArrayList<>();
		this.substring = "";
		this.addElemento(elemento);
	}
	
	public void setSubstring(String substring) {
		this.substring = substring;
	}
	
	public String getSubstring() {
		return this.substring;
	}
	
	public void addElemento(String elemento) {
		this.contenido.add(elemento);
	}
	
	public boolean deleteElemento(String elemento) {
		return this.contenido.remove(elemento);
	}
	
	public String getElemento(int index) {
		return this.contenido.get(index);
	}
	
	public boolean searchElemento(String elemento) {
		return this.contenido.contains(elemento);
	}
	
	public int getSize() {
		return this.contenido.size();
	}
}