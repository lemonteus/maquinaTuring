import java.util.ArrayList;
import org.json.*;
import java.util.Scanner;

import java.util.Iterator;

import java.io.File;  
import java.io.FileNotFoundException; 

public class MaquinaTuring {

    public static void main(String args[])
    {
        //System.out.println(args[1]);
        boolean emptyStr = false;

        if (args.length == 2) {
            
            if(args[1].equals("")) 
                emptyStr = true;
                //System.out.println(args[1]);
                //System.out.println("Usar: java mt [MT] [Word]"); 

            File file = new File(args[0]);
            String[] rawWord = args[1].split("");
                        
            try {
                Scanner s = new Scanner(file);
                String stringJson = "";

                while (s.hasNextLine())
                    stringJson = stringJson + s.nextLine();         

                s.close();

                JSONObject json = new JSONObject(stringJson);

                JSONArray jsonArrays = json.getJSONArray("mt");
                ArrayList<Object> arrays = new ArrayList<Object>();
                
                jsonArrays.forEach((array) -> arrays.add(array));
                
                //Estados TALVEZ VAI TER QUE MUDAR POR CAUSA DE COMPATIBILIDADE
                StateList sl = new StateList((JSONArray) arrays.get(0));

                JSONArray auxArray;

                //Alfabeto de Entrada
                ArrayList<String> alfabetoEntrada = new ArrayList<String>();
                auxArray = (JSONArray) arrays.get(1);
                auxArray.forEach((simbolo) -> alfabetoEntrada.add((String) simbolo));
               
                //Alfabeto da Fita
                ArrayList<String> alfabetoFita = new ArrayList<String>();
                auxArray = (JSONArray) arrays.get(2);
                auxArray.forEach((simbolo) -> alfabetoFita.add((String) simbolo));

                String startMarker = (String) arrays.get(3);              
                String emptySymbol = (String) arrays.get(4);

                //Transicoes
                TransitionList tl = new TransitionList ((JSONArray) arrays.get(5), sl);

                String startState = (String) arrays.get(6);


                //Estados finais
                auxArray = (JSONArray) arrays.get(7);
                
                for(Object finalState : auxArray)
                {
                    sl.getState((String) finalState).setFinal(true);
                }

                //Instanciando fita, colocando marcador de inicio
                ArrayList<String> tape = new ArrayList<String>();
                tape.add(startMarker);

                //Processando a palavra, escrevendo os caracteres na fita 
                //TODO: MUDAR ISSO DPS

                if (!emptyStr) {
                    for (int i = 0; i < rawWord.length; i++){
                        if (alfabetoFita.indexOf(rawWord[i]) != -1)
                        {
                            tape.add(rawWord[i]);
                        }
                        //System.out.println(rawWord[i]);
                    }
                } else {
                    tape.add(emptySymbol);
                }

                //Encontrar o estado inicial, pegar o primeiro simbolo da palavra e seguir a partir dela
                State currentState = sl.getState(startState);
                Transition currentTransition = null;
                State destState;
                //TODO: implementar o rawWord depois
                int tapeHead = 1;
                String symbol = tape.get(tapeHead);
                boolean recognizes = false;
                boolean done = false;

                while(!done)
                {
                    currentTransition = null;
                    //System.out.println(tape +"\n Head: "+tapeHead);  
                    //String buffer = new Scanner(System.in).nextLine();

                    if (currentState.getFinal())
                        recognizes = true;
                    else
                        recognizes = false;

                    if (tapeHead >= tape.size())
                    {
                        tape.add(emptySymbol);
                    }     
                    
                    symbol = tape.get(tapeHead);
                    //System.out.println(symbol);   
                    currentTransition = tl.getTransition(currentState.getName(), symbol);
                    
                    if ((currentTransition != null)) {

                        destState = currentTransition.getTransitionDestState();

                        if(!destState.equals(null)) {
                            

                            tape.remove(tapeHead);
                            
                            tape.add(tapeHead, currentTransition.getWriteSymbol());
                            if(tapeHead > 0)
                                tapeHead = tapeHead + currentTransition.getTapeHeadDirection();
                            else 
                                tapeHead = 0;
    
                            currentState = destState;      
                                        
                        } else {
                            recognizes = false;
                        }
                    }
                    else done = true;                    
                }

                if(done && recognizes)
                {
                    System.out.println("Sim");
                } else {
                    System.out.println("Não");
                }

            } catch (FileNotFoundException e)
            {
                System.out.println("ERRO: Arquivo \"" + args[0] + "\" não encontrado");
            }

        } else {
            //System.out.println(args[1]);
            System.out.println("Usar: java mt [MT] [Word]");

        }

    }

}

class State {

    private String name;
    private boolean isFinal;
    private boolean isStart;

    public State(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setFinal (boolean isFinal)
    {
        this.isFinal = isFinal;
    }

    public void setStart (boolean isStart)
    {
        this.isStart = true;
    }

    public boolean getFinal()
    {
        return isFinal;
    }

    public boolean getStart ()
    {
        return isStart;
    }
}

class StateList {
    
    private ArrayList<State> states;

    public StateList(JSONArray statesArray)
    {
        this.states = new ArrayList<State>();

        statesArray.forEach((name) -> {
            states.add(new State((String) name));
            //System.out.println(name);
        });
    }

    public State getState (String name)
    {
        Iterator<State> it = states.iterator();
        State returnState = null;

        while(it.hasNext()) 
        {
            State aux = it.next();

            if(aux.getName().equals(name))
                returnState = aux; 
        }

        return returnState;
    }

    public void setFinalState(String name)
    {
        getState(name).setFinal(true);
    }

}

class Transition {

    private State startState; 
    private String readSymbol; 
    private State destState; 
    private String writeSymbol; 
    private String headDirection;

    public Transition(JSONArray transition, StateList sl) {

        this.startState = sl.getState((String) transition.get(0));
        this.readSymbol = (String) transition.get(1);
        this.destState = sl.getState((String) transition.get(2));
        this.writeSymbol = (String) transition.get(3);
        this.headDirection = (String) transition.get(4);
    }

    public State getTransitionStartState()
    {
        return this.startState;
    }

    public String getTransitionReadSymbol()
    {
        return this.readSymbol;
    }

    public State getTransitionDestState()
    {
        return this.destState;
    }

    public String getReadSymbol()
    {
        return this.readSymbol;
    }

    public String getWriteSymbol()
    {
        return this.writeSymbol;
    }

    public int getTapeHeadDirection()
    {
        if (this.headDirection.equals(">"))
            return 1;
        else return -1;
    }
}

class TransitionList {

    ArrayList<Transition> transitions;

    public TransitionList(JSONArray transitionArrays, StateList sl)
    {
        this.transitions = new ArrayList<Transition>();
        transitionArrays.forEach((transition) -> transitions.add(new Transition((JSONArray) transition, sl)));
    }

    public Transition getTransition (String name, String symbol)
    {
        Iterator<Transition> it = transitions.iterator();
        Transition transition = null;

        while(it.hasNext())
        {
            Transition aux = it.next();
            if(aux.getTransitionStartState().getName().equals(name) && aux.getTransitionReadSymbol().equals(symbol))
            {
                transition = aux;
            }
        }

        return transition;
    }
}

