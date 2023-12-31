package com.example.course_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;
import java.net.ConnectException;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username)
    {
        Parent root = null;

        if (username!=null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root=loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else
        {
            try {
                root=FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password)
    {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        try{
              connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/course_project");
              psCheckUserExists = connection.prepareStatement("SELECT * FROM authorization WHERE username = ?");
              psCheckUserExists.setString(1, username);
              resultSet = psCheckUserExists.executeQuery();

              if(resultSet.isBeforeFirst()){
                  System.out.println("User already exists!");
                  Alert alert = new Alert(Alert.AlertType.ERROR);
                  alert.setContentText("Вы не можете использовать этот логин");
                  alert.show();
              }
              else
              {
                  psCheckUserExists = connection.prepareStatement("INSERT INTO authorization (username, password)");
                  psInsert.setString(1, username);
                  psInsert.setString(2, password);
                  psInsert.executeUpdate();

                  changeScene(event, "logged-in", "welcome", username);
              }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null)
            {
                try{
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null)
            {
                try{
                    psInsert.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null)
            {
                try{
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/course_project");
            preparedStatement = connection.prepareStatement("SELECT password FROM authorization WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.isBeforeFirst()){
                System.out.println("Такого пользователя не существует");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("предоставленные учетные данные не корректны");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrivedPassword = resultSet.getString("password");
                    if (retrivedPassword.equals(password)){
                        changeScene(event, "logged-in.fxml", "Welcome!", username);
                    } else {
                        System.out.println("Пароль не совпал");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("предоставленные учетные данные не корректны");
                        alert.show();
                    }
                }
            }
    } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null)
            {
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        }
}
