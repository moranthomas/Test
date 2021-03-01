
package Guice;

public class EmailCommunicationMode implements CommunicationMode {

    /*@Override
    public CommunicationModel getMode() {
        return CommunicationModel.EMAIL;
    }*/

    @Override
    public boolean sendMessage(String Message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
