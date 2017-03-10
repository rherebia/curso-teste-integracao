package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoDaoTest {

	private Session session;
	private UsuarioDao usuarioDao;
	private LeilaoDao leilaoDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		
		usuarioDao = new UsuarioDao(session);
		leilaoDao = new LeilaoDao(session);
		
		session.beginTransaction();
	}
	
	@After
	public void depois() {
		session.getTransaction().rollback();
		session.close();
	}
	
	@Test
	public void deveContarLeiloesNaoEncerrados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao ativo = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Geladeira")
				.comValor(1500D)
				.constroi();
		
		Leilao encerrado = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.constroi();
		
		encerrado.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		long total = leilaoDao.total();
		
		assertEquals(1L, total);
	}
	
	@Test
	public void deveRetornarZeroComTodosLeiloesEncerrados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao ativo = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Geladeira")
				.comValor(1500D)
				.constroi();
		
		Leilao encerrado = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.constroi();
		
		ativo.encerra();
		encerrado.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		long total = leilaoDao.total();
		
		assertEquals(0L, total);
	}
	
	@Test
	public void deveRetornarLeiloesNaoUsados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao ativo = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Geladeira")
				.comValor(1500D)
				.usado()
				.constroi(); 
		
		Leilao encerrado = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.constroi(); 
		
		ativo.encerra();
		encerrado.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		List<Leilao> novos = leilaoDao.novos();
		
		assertEquals(1L, novos.size());
		assertEquals("Xbox", novos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeiloesAntigos() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao ativo = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Geladeira")
				.comValor(1500D)
				.constroi();
		
		Leilao encerrado = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(8)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		List<Leilao> novos = leilaoDao.antigos();
		
		assertEquals(1L, novos.size());
		assertEquals("Xbox", novos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeilaoComExatos7Dias() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao encerrado = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(7)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(encerrado);
		
		List<Leilao> novos = leilaoDao.antigos();
		
		assertEquals(1L, novos.size());
		assertEquals("Xbox", novos.get(0).getNome());
	}
	
	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.constroi();
				
		Leilao leilao2 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Geladeira")
				.comValor(1700D)
				.diasAtras(20)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(1, leiloes.size());
		assertEquals("Xbox", leiloes.get(0).getNome());
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.constroi();
		
		leilao1.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(0, leiloes.size());
	}
	
	@Test
	public void naoDeveTrazerLeiloesComTresLancesEntreValores() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		Usuario jose = new Usuario("Jose", "jose@jose.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.lance(Calendar.getInstance(), mauricio, 100)
				.lance(Calendar.getInstance(), jose, 200)
				.lance(Calendar.getInstance(), mauricio, 300)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloes = leilaoDao.disputadosEntre(0, 800);
		
		assertEquals(0, leiloes.size());
	}
	
	@Test
	public void deveTrazerLeiloesComMaisDeTresLancesEntreValores() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		Usuario jose = new Usuario("Jose", "jose@jose.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.lance(Calendar.getInstance(), mauricio, 100)
				.lance(Calendar.getInstance(), jose, 200)
				.lance(Calendar.getInstance(), mauricio, 300)
				.lance(Calendar.getInstance(), mauricio, 400)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloes = leilaoDao.disputadosEntre(0, 800);
		
		assertEquals(1, leiloes.size());
		assertEquals("Xbox", leiloes.get(0).getNome());
	}
	
	@Test
	public void deveTrazerLeiloesComMaisDeTresLancesNoInicioDoIntervalo() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		Usuario jose = new Usuario("Jose", "jose@jose.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.lance(Calendar.getInstance(), mauricio, 100)
				.lance(Calendar.getInstance(), jose, 200)
				.lance(Calendar.getInstance(), mauricio, 300)
				.lance(Calendar.getInstance(), mauricio, 400)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloes = leilaoDao.disputadosEntre(700, 900);
		
		assertEquals(1, leiloes.size());
		assertEquals("Xbox", leiloes.get(0).getNome());
	}
	
	@Test
	public void deveTrazerLeiloesComMaisDeTresLancesNoFimDoIntervalo() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		Usuario jose = new Usuario("Jose", "jose@jose.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.lance(Calendar.getInstance(), mauricio, 100)
				.lance(Calendar.getInstance(), jose, 200)
				.lance(Calendar.getInstance(), mauricio, 300)
				.lance(Calendar.getInstance(), mauricio, 400)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloes = leilaoDao.disputadosEntre(500, 700);
		
		assertEquals(1, leiloes.size());
		assertEquals("Xbox", leiloes.get(0).getNome());
	}
	
	@Test
	public void deveDevolverLeilaoEmQueUmUsuarioParticipou() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@mauricio.com.br");
		Usuario jose = new Usuario("Jose", "jose@jose.com.br");
		
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(mauricio)
				.comNome("Xbox")
				.comValor(700D)
				.diasAtras(2)
				.lance(Calendar.getInstance(), mauricio, 100)
				.lance(Calendar.getInstance(), jose, 200)
				.lance(Calendar.getInstance(), mauricio, 300)
				.lance(Calendar.getInstance(), mauricio, 400)
				.constroi();
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloesDoUsuario = leilaoDao.listaLeiloesDoUsuario(mauricio);
		
		assertEquals(1, leiloesDoUsuario.size());
	}
	
	@Test
    public void listaSomenteOsLeiloesDoUsuario() throws Exception {
        Usuario dono = new Usuario("Mauricio", "m@a.com");
        Usuario comprador = new Usuario("Victor", "v@v.com");
        Usuario comprador2 = new Usuario("Guilherme", "g@g.com");
        Leilao leilao = new LeilaoBuilder()
            .comDono(dono)
            .comValor(50.0)
            .lance(Calendar.getInstance(), comprador, 100.0)
            .lance(Calendar.getInstance(), comprador2, 200.0)
            .constroi();
        Leilao leilao2 = new LeilaoBuilder()
            .comDono(dono)
            .comValor(250.0)
            .lance(Calendar.getInstance(), comprador2, 100.0)
            .constroi();
        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        usuarioDao.salvar(comprador2);
        leilaoDao.salvar(leilao);
        leilaoDao.salvar(leilao2);

        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador);
        assertEquals(1, leiloes.size());
        assertEquals(leilao, leiloes.get(0));
    }
	
	@Test
    public void devolveAMediaDoValorInicialDosLeiloesQueOUsuarioParticipou(){
        Usuario dono = new Usuario("Mauricio", "m@a.com");
        Usuario comprador = new Usuario("Victor", "v@v.com");
        Leilao leilao = new LeilaoBuilder()
            .comDono(dono)
            .comValor(50.0)
            .lance(Calendar.getInstance(), comprador, 100.0)
            .lance(Calendar.getInstance(), comprador, 200.0)
            .constroi();
        Leilao leilao2 = new LeilaoBuilder()
            .comDono(dono)
            .comValor(250.0)
            .lance(Calendar.getInstance(), comprador, 100.0)
            .constroi();
        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        leilaoDao.salvar(leilao);
        leilaoDao.salvar(leilao2);

        assertEquals(150.0, leilaoDao.getValorInicialMedioDoUsuario(comprador), 0.001);
    }
	
	@Test
	public void deveDeletarLeilao() {
		Usuario dono = new Usuario("Mauricio", "m@a.com");
		
		Leilao leilao = new LeilaoBuilder()
	            .comDono(dono)
	            .comValor(50.0)
	            .constroi();
		
		usuarioDao.salvar(dono);
		
		leilaoDao.salvar(leilao);
		
		session.flush();
		session.clear();
		
		leilaoDao.deleta(leilao);
		
		assertNull(leilaoDao.porId(leilao.getId()));
	}
}
