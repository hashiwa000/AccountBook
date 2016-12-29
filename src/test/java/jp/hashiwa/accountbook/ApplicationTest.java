package jp.hashiwa.accountbook;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

  private static final MediaType TEXT_HTML_UTF8 = new MediaType(MediaType.TEXT_HTML, Charset.forName("utf-8"));

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnDefaultMessage() throws Exception {
    this.mockMvc.perform(get("/"))
      .andDo(print())
      .andExpect(status().isOk());
      //.andExpect(content().string(containsString("List of Applications")));
  }

  @Test
  public void testRedirect() throws Exception {
    this.mockMvc.perform(get("/accountbook/"))
      .andExpect(status().  is3xxRedirection());
    this.mockMvc.perform(get("/accountbook/show"))
      .andExpect(status().  is3xxRedirection());
    this.mockMvc.perform(get("/accountbook/create"))
      .andExpect(status().  is3xxRedirection());
    this.mockMvc.perform(get("/accountbook/stats"))
      .andExpect(status().  is3xxRedirection());
  }

  @Test
  @WithMockUser(username="user")
  public void testABItemShow() throws Exception {
    this.mockMvc.perform(get("/accountbook/show"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("<title>Account Book</title>")))
      .andExpect(content().string(containsString("table table-striped")))
      .andExpect(content().string(containsString("Date")))
      .andExpect(content().string(containsString("Amount")))
      .andExpect(content().string(containsString("Name")))
      .andExpect(content().string(containsString("Type")))
      .andExpect(content().string(containsString("Description")))
      .andExpect(content().string(containsString("Remarks")));
  }

  @Test
  @WithMockUser(username="user")
  public void testABItemCreate() throws Exception {
    this.mockMvc.perform(get("/accountbook/create"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("<title>Account Book</title>")))
      .andExpect(content().string(containsString("<form action=\"/accountbook/create\" method=\"post\">")))
      .andExpect(content().string(containsString("<label for=\"inputDate\">Date</label>")))
      .andExpect(content().string(containsString("<label for=\"inputAmount\">Amount</label>")))
      .andExpect(content().string(containsString("<label for=\"inputName\">Name</label>")))
      .andExpect(content().string(containsString("<label for=\"inputType\">Type</label>")))
      .andExpect(content().string(containsString("<label for=\"inputDesc\">Description</label>")))
      .andExpect(content().string(containsString("<label for=\"inputRemarks\">Remarks</label>")));
  }

  @Test
  @WithMockUser(username="user")
  public void testABItemCreate2() throws Exception {
    this.mockMvc.perform(post("/accountbook/create").param("date", "2016-12-15").param("amount", "1000").param("name", "test").param("type", "xxx").param("desc", "").param("remarks", ""))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("Created:")))
      .andExpect(content().string(containsString("xxx")));
    this.mockMvc.perform(get("/accountbook/show"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("xxx")));
  }
}
