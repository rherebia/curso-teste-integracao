package br.com.caelum.pm73.dao;

import java.util.Calendar;

import br.com.caelum.pm73.dominio.Lance;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoBuilder {

	private Leilao leilao;

    public LeilaoBuilder() {
    	this.leilao = new Leilao("XBox", 1500.0, new Usuario("Joao da Silva", "joao@silva.com.br"), false);
    	this.leilao.setDataAbertura(Calendar.getInstance());
    }

    public LeilaoBuilder comDono(Usuario dono) {
        this.leilao.setDono(dono);
        return this;
    }

    public LeilaoBuilder comValor(double valor) {
        this.leilao.setValorInicial(valor);
        return this;
    }

    public LeilaoBuilder comNome(String nome) {
        this.leilao.setNome(nome);
        return this;
    }

    public LeilaoBuilder usado() {
    	this.leilao.setUsado(true);
        return this;
    }

    public LeilaoBuilder encerrado() {
    	this.leilao.encerra();
        return this;
    }

    public LeilaoBuilder diasAtras(int dias) {
        Calendar data = Calendar.getInstance();
        data.add(Calendar.DAY_OF_MONTH, -dias);

        this.leilao.setDataAbertura(data);

        return this;
    }
    
    public LeilaoBuilder lance(Calendar data, Usuario usuario, double valor) {
    	this.leilao.adicionaLance(new Lance(data, usuario, valor, leilao));
    	
    	return this;
    }

    public Leilao constroi() {
        return leilao;
    }

}