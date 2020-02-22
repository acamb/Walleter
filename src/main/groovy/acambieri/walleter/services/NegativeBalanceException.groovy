package acambieri.walleter.services

class NegativeBalanceException extends Exception{

    NegativeBalanceException(String message){
        super(message);
    }
}
