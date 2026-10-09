package com.clase.modelo;

public class Doctor {
    private int iddoc;
    private String nomedoc;
    private String apeldoc;
    private boolean coledoc;
    private String movildoc;
    private String maildoc;
    private String espedoc;

    public Doctor(int iddoc, String nomedoc, String apeldoc, boolean coledoc,
            String movildoc, String maildoc, String espedoc) {
        this.iddoc = iddoc;
        this.nomedoc = nomedoc;
        this.apeldoc = apeldoc;
        this.coledoc = coledoc;
        this.movildoc = movildoc;
        this.maildoc = maildoc;
        this.espedoc = espedoc;
    }

    // Doctor nuevo: el id lo genera la base de datos (AUTO_INCREMENT)
    public Doctor(String nomedoc, String apeldoc, boolean coledoc,
            String movildoc, String maildoc, String espedoc) {
        this(0, nomedoc, apeldoc, coledoc, movildoc, maildoc, espedoc);
    }

    public int getId() {
        return iddoc;
    }

    public void setId(int iddoc) {
        this.iddoc = iddoc;
    }

    public String getNombre() {
        return nomedoc;
    }

    public void setNombre(String nomedoc) {
        this.nomedoc = nomedoc;
    }

    public String getApellidos() {
        return apeldoc;
    }

    public void setApellidos(String apeldoc) {
        this.apeldoc = apeldoc;
    }

    public boolean isColegiado() {
        return coledoc;
    }

    public void setColegiado(boolean coledoc) {
        this.coledoc = coledoc;
    }

    public String getMovil() {
        return movildoc;
    }

    public void setMovil(String movildoc) {
        this.movildoc = movildoc;
    }

    public String getEmail() {
        return maildoc;
    }

    public void setEmail(String maildoc) {
        this.maildoc = maildoc;
    }

    public String getEspecialidad() {
        return espedoc;
    }

    public void setEspecialidad(String espedoc) {
        this.espedoc = espedoc;
    }
}
