package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		
		usuarioDao = new UsuarioDao(session);
		
		session.beginTransaction();
	}
	
	@After
	public void depois() {
		session.getTransaction().rollback();
		
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {
		Usuario novoUsuario = new Usuario("Joao da Silva", "joao@dasilva.com.br");
		
		usuarioDao.salvar(novoUsuario);
		
		Usuario usuario = usuarioDao.porNomeEEmail("Joao da Silva", "joao@dasilva.com.br");
		assertEquals("Joao da Silva", usuario.getNome());
		assertEquals("joao@dasilva.com.br", usuario.getEmail());
	}
	
	@Test
	public void deveRetornarNullQuandoUsuarioNaoForEncontrado() {
		Usuario usuario = usuarioDao.porNomeEEmail("Jose da Silva", "jose@dasilva.com.br");
		
		assertNull(usuario);
	}
}
