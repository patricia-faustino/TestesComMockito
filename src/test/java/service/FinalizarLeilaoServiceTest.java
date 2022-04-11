package service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FinalizarLeilaoServiceTest  {

    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    public void deveriaFinalizarUmLeilao() {
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        Leilao leilao = leiloes.get(0);

        service.finalizarLeiloesExpirados();

        verify(leilaoDao, times(1)).buscarLeiloesExpirados();
        verify(leilaoDao, times(1)).salvar(leilao);
        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());
    }

    @Test
    public void deveEnviarEmail() {
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        Leilao leilao = leiloes.get(0);


        service.finalizarLeiloesExpirados();
        Lance lanceVencedor = leilao.getLanceVencedor();

        verify(leilaoDao, times(1)).buscarLeiloesExpirados();
        verify(leilaoDao, times(1)).salvar(leilao);
        verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    public void naoDeveEnviarEmailCasoExcecaoSejaLancadaAoSalvarLeilao() {
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        when(leilaoDao.salvar(any())).thenThrow(RuntimeException.class);
        Leilao leilao = leiloes.get(0);

        assertThrows(RuntimeException.class,
                () -> service.finalizarLeiloesExpirados());
    }

    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;

    }
}
