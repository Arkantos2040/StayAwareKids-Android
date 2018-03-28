package cl.m4uro.stayawarekids.Model;

/**
 * Created by m4uro on 07-03-18.
 */

public class ChildrenModel {

    private int childrenID;
    private String urlAvatar;
    private String nombreApellido;
    private String Rut;

    public ChildrenModel(int childrenID, String urlAvatar, String nombreApellido, String Rut){
        this.childrenID = childrenID;
        this.urlAvatar = urlAvatar;
        this.nombreApellido = nombreApellido;
        this.Rut = Rut;
    }

    public int getChildrenID(){
        return childrenID;
    }

    public void setChildrenID(int childrenID){
        this.childrenID = childrenID;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public String getRut() {
        return Rut;
    }

    public void setRut(String rut) {
        Rut = rut;
    }
}
