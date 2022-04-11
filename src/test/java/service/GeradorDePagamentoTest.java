package service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GeradorDePagamentoTest {

    @InjectMocks
    private GeradorDePagamento geradorDePagamento;

    @Mock
    private PagamentoDao pagamentoDao;

    //permite capturar argumentos criados em métodos para validação, etc
    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Test
    public void deveCriarPagamentoParaVencedorDoLeilao() {
        Leilao leilao = leilao();

        geradorDePagamento.gerarPagamento(leilao.getLanceVencedor());

        verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        Lance vencedor = leilao.getLanceVencedor();

        assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        assertEquals(vencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(vencedor.getUsuario(),
                pagamento.getUsuario());
        assertEquals(leilao,
                pagamento.getLeilao());
    }

    private Leilao leilao() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);

        return leilao;
    }
}
