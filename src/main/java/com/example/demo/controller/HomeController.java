package com.example.demo.controller;

import com.example.demo.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

  @Autowired
  HikariDataSource dataSource;
  Connection connection = null;
  PreparedStatement statement = null;
  ResultSet resultSet = null;
  String sql;

  @GetMapping("/display") // reading from db
  public String displayAll(Model model) throws SQLException {
    List<User> users = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("select * from userdb");
      statement.execute();
      resultSet = statement.getResultSet();
      while (resultSet.next()) {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("user_name"));
        user.setPosition(resultSet.getString("user_position"));
        users.add(user);
      }
      model.addAttribute("users", users);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connection.close();
      statement.close();
      resultSet.close();

    }
    return "index";
  }

  @GetMapping("/add")
  public String addPage(Model model) {
    model.addAttribute("user", new User());
    return "add";
  }

  @PostMapping("/add") //writing to db
  public String addUser(@ModelAttribute User user) throws SQLException { // receiving form data as user object
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("insert into userdb(user_id,user_name,user_position) values(?,?,?)");
      statement.setInt(1, user.getId());
      statement.setString(2, user.getName());
      statement.setString(3, user.getPosition());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      statement.close();
      connection.close();
    }
    return "redirect:/display";
  }

  @GetMapping("/delete")

  public String delete(@ModelAttribute User user, HttpServletRequest request) throws SQLException {
    try {
      user.setId(Integer.parseInt(request.getParameter("id")));
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("delete from userdb where user_id =?");
      statement.setInt(1, user.getId());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      statement.close();
      connection.close();
    }
    return "redirect:/display";
  }

  @GetMapping("/update")
  public String updatePage(Model model,HttpServletRequest request) throws SQLException {
    ArrayList<User> users = new ArrayList<>();
    int id = Integer.parseInt(request.getParameter("id"));
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("select * from userdb where user_id = ?");
      statement.setInt(1, id);
      statement.execute();
      resultSet = statement.getResultSet();
      while (resultSet.next()){
        User user = new User();
        user.setName(resultSet.getString("user_name"));
        user.setPosition(resultSet.getString("user_position"));
        user.setId(resultSet.getInt("user_id"));
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      statement.close();
      connection.close();
    }
    model.addAttribute("users",users);
    model.addAttribute("user", new User());
    return "update";
  }

  @PostMapping("/update")
  public String updateUser(@ModelAttribute User user, HttpServletRequest request) throws SQLException {
    try {
      user.setId(Integer.parseInt(request.getParameter("id")));
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("update userdb set  user_name =?, user_position=? where user_id = ? ");
      statement.setString(1, user.getName() );
      statement.setString(2, user.getPosition());
      statement.setInt(3, user.getId());
      statement.execute();
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      statement.close();
      connection.close();
    }
    return "redirect:/display";
  }


  @GetMapping("/deleteAll")
  public String deleteAllPage() throws SQLException {
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("delete from userdb");
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      statement.close();
      connection.close();
    }
    return "redirect:/display";
  }

}
