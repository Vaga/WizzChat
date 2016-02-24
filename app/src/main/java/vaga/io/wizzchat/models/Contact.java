package vaga.io.wizzchat.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Contact extends RealmObject {

    @PrimaryKey
    private String email;

    private String name;
    private String publicKey;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
