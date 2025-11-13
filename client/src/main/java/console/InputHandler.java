package console;

import serverfacade.HTTPException;

public interface InputHandler {
    void parse(String[] inputs) throws InvalidInputException, TerminationException, HTTPException;
    String getUser();
}
