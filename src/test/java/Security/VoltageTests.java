package Security;

import Security.VirtualFile;
import org.junit.jupiter.api.Test;

public class VoltageTests {

    /*@Test
    public VirtualFile getReceipt(int transactionId, DCalContext context)throws RemoteException, SQLException,
            DCalMissingContextException, Exception, MaxFileSizeException{
        VirtualFile file = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Blob attachment = null;
        InputStream stream = null;
        try{
            con = getConnection(context);
            ps = con.prepareStatement("select file_name, file_content from transaction_receipt where transaction_id = ?");
            ps.setInt(1, transactionId);

            rs = ps.executeQuery();

            if(rs.next()){
                attachment = rs.getBlob("file_content");
                byte[] blobAsBytes = attachment.getBytes(1, (int)attachment.length());
                byte[] decrypedBlob = null;
                try {
                    decrypedBlob = VoltageHelper.getInstance().decrypt(blobAsBytes);
                } catch (VoltageException e) {
                    // set the plain blob as it is if decryption fails.
                    decrypedBlob = blobAsBytes;
                }
                file = new VirtualFile();
                file.setFileName(rs.getString("file_name"));
                stream = new ByteArrayInputStream(decrypedBlob);
                file.loadBuffer(stream);
            }
        }
        catch(MaxFileSizeException ex){
            throw ex;
        }
        catch(Exception ex){
            logger.log(Level.SEVERE, "Unable to get receipt for transaction " + transactionId, ex);
        }
        finally{
            DCalFSUtil.close(stream);
            cleanUp(con, ps, rs);
        }

        return file;
    }*/

}
