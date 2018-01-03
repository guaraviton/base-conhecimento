package br.com.softtek;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.softtek.config.serialization.CampoConsulta;
import br.com.softtek.jira.Connection;
import br.com.softtek.jira.Method;
import br.com.softtek.jira.Response;
import br.com.softtek.jira.serialization.Jira;
import br.com.softtek.jira.serialization.JsonUtil;

public class JiraUtilTest {

	private static final String URL_GET = "https://jira.ep.petrobras.com.br:8443/rest/api/2/search?jql=project+in%28%22GIPCR%22%29+and+status+in%28%22Em+desenvolvimento%22%29&fields=project,project,summary,key,status,resolution,issuetype,created,customfield_13300,duedate,customfield_17478&expand=changelog";
	
	@Test
	public void testInclusaoGrupoCA() throws Exception {
		Connection.init("y2jm", "vAsco111");
		Response response = Connection.getInstance().call(URL_GET, Method.GET);
		List<CampoConsulta> camposConsulta = new ArrayList<CampoConsulta>();
		camposConsulta.add(new CampoConsulta("summary"));
		camposConsulta.add(new CampoConsulta("customfield_17478"));
		List<Jira> jiras = JsonUtil.read(response.getResposeText(), camposConsulta, true);
		System.out.println(jiras);
	}
}
