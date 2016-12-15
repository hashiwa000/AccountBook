package jp.hashiwa.todolist;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
  public void testTodoListShow() throws Exception {
    this.mockMvc.perform(get("/todolist/show"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("<title>Todo List</title>")))
      .andExpect(content().string(containsString("table table-striped")))
      .andExpect(content().string(containsString("Id")))
      .andExpect(content().string(containsString("Title")))
      .andExpect(content().string(containsString("Memo")))
      .andExpect(content().string(containsString("Deadline")));
  }

  @Test
  public void testTodoListCreate() throws Exception {
    this.mockMvc.perform(get("/todolist/create"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_HTML_UTF8))
      .andExpect(content().string(containsString("<title>Todo List</title>")))
      .andExpect(content().string(containsString("<form action=\"/todolist/create\" method=\"post\">")))
      .andExpect(content().string(containsString("<label for=\"inputTitle\">Title</label>")))
      .andExpect(content().string(containsString("<label for=\"inputMemo\">Memo</label>")))
      .andExpect(content().string(containsString("<label for=\"inputDeadline\">Deadline</label>")));
  }


}
