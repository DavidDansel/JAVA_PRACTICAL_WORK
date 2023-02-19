package fr.isen.java2.db.daos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres() {
		String sqlQuery="SELECT * FROM genre";
		List<Genre> genres=new ArrayList<>();
		int numberOfRows=0;
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			try (ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					numberOfRows++;
					Integer genreId = resultSet.getInt("idgenre");
					String genreName= resultSet.getString("name");
					genres.add(new Genre(genreId,genreName));
				}
			} catch (SQLException e) {
				System.out.println("An exception occurred generating resultSet");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connection to database");
		}
		if(numberOfRows==0)
			return null;
		return genres;
	}

	public Genre getGenre(String name) {
		String sqlQuery="SELECT * FROM genre WHERE name = ?";
		Integer genreId=null;
		String genreName=null;
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1,name);
			try (ResultSet resultSet = statement.executeQuery()) {
				if(resultSet.next()){
					genreId = resultSet.getInt("idgenre");
					genreName= resultSet.getString("name");
				}else
					return  null;

			} catch (SQLException e) {
				System.out.println("An exception occurred when generating resultSet");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connecting to database");
		}
		return new Genre(genreId,genreName);
	}

	public void addGenre(String name) {
		String sqlQuery="INSERT INTO genre(name) VALUES(?)";
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1,name);
			try {
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("An exception occurred when adding to Genre");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connecting to database");
		}
	}
}