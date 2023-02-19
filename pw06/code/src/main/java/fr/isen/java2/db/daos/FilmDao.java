package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	public List<Film> listFilms() {
		String sqlQuery = "SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre";
		List<Film> films = new ArrayList<>();
		int numberOfRows = 0; //keep track of the number of rows returned by the query
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					numberOfRows++;
					Integer filmId = resultSet.getInt("idfilm");
					String title = resultSet.getString("title");
					Date releaseDate = resultSet.getTimestamp("release_date");
					Integer genreId = resultSet.getInt("genre_id");
					Integer duration = resultSet.getInt("duration");
					String director = resultSet.getString("director");
					String summary = resultSet.getString("summary");
					String genreName = resultSet.getString("name");
					films.add(new Film(filmId, title, new java.sql.Date(releaseDate.getTime()).toLocalDate(),
							new Genre(genreId, genreName), duration, director, summary));
				}
			} catch (SQLException e) {
				System.out.println("An exception occurred generating resultSet");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connection to database");
		}
		if (numberOfRows == 0)//if no row was returned
			return null;
		return films;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		String sqlQuery = "SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?";
		List<Film> films = new ArrayList<>();
		int numberOfRows = 0;
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, genreName);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					numberOfRows++;
					Integer filmId = resultSet.getInt("idfilm");
					String title = resultSet.getString("title");
					Date releaseDate = resultSet.getTimestamp("release_date");
					Integer genreId = resultSet.getInt("genre_id");
					Integer duration = resultSet.getInt("duration");
					String director = resultSet.getString("director");
					String summary = resultSet.getString("summary");
					films.add(new Film(filmId, title, new java.sql.Date(releaseDate.getTime()).toLocalDate(),
							new Genre(genreId, genreName), duration, director, summary));
				}
			} catch (SQLException e) {
				System.out.println("An exception occurred generating resultSet");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connection to database");
		}
		if (numberOfRows == 0)
			return null;
		return films;
	}

	public Film addFilm(Film film) {
		String sqlQuery = "INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
		String title = film.getTitle();
		long filmId;
		Timestamp release_date = new Timestamp(java.sql.Date.valueOf(film.getReleaseDate()).getTime());
		Integer genre_id = film.getGenre().getId();
		Integer duration = film.getDuration();
		String director = film.getDirector();
		String summary = film.getSummary();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			PreparedStatement statement = connection.prepareStatement(sqlQuery,Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, title);
			statement.setTimestamp(2, release_date);
			statement.setInt(3, genre_id);
			statement.setInt(4, duration);
			statement.setString(5, director);
			statement.setString(6, summary);
			try {
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
				if (ids.next()) {
					filmId = ids.getLong(1);
					return new Film((int)filmId, film.getTitle(), film.getReleaseDate(),film.getGenre(),
							film.getDuration(),film.getDirector(),film.getSummary());
				}
			} catch (SQLException e) {
				System.out.println("An exception occurred when adding to Genre");
			}
		} catch (SQLException e) {
			System.out.println("An exception occurred when connecting to database");
		}
		return null;
	}
}