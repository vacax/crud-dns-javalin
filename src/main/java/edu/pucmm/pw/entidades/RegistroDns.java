package edu.pucmm.pw.entidades;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("registros")
public class RegistroDns {

    @Id
    ObjectId id;
    String host;
    int ttl;
    String dominio;
    String ip;
    String creadoPor;
    Date fechaCreacion;
    Long idDigitalOcean;

    public RegistroDns() {
    }

    public RegistroDns(String host, String ip, String dominio, String creadoPor, Date fechaCreacion , int ttl) {
        this.host = host;
        this.ttl = ttl;
        this.dominio = dominio;
        this.ip = ip;
        this.creadoPor = creadoPor;
        this.fechaCreacion = fechaCreacion;
    }

    public RegistroDns(ObjectId id, String host, int ttl, String dominio, String ip, String creadoPor, Date fechaCreacion, Long idDigitalOcean) {
        this.id = id;
        this.host = host;
        this.ttl = ttl;
        this.dominio = dominio;
        this.ip = ip;
        this.creadoPor = creadoPor;
        this.fechaCreacion = fechaCreacion;
        this.idDigitalOcean = idDigitalOcean;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdDigitalOcean() {
        return idDigitalOcean;
    }

    public void setIdDigitalOcean(Long idDigitalOcean) {
        this.idDigitalOcean = idDigitalOcean;
    }

    @Override
    public String toString() {
        return "RegistroDns{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", ttl=" + ttl +
                ", dominio='" + dominio + '\'' +
                ", ip='" + ip + '\'' +
                ", creadoPor='" + creadoPor + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", idDigitalOcean=" + idDigitalOcean +
                '}';
    }
}
