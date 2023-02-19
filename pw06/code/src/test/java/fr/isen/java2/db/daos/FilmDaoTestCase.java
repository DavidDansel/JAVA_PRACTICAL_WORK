package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

public class FilmDaoTestCase {

	private FilmDao filmDao = new FilmDao();
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS film (\r\n"
						+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
						+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
						+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
						+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}

	@Test
	public void shouldListFilms() {
		List<Film> films = filmDao.listFilms();
		assertThat(films).hasSize(3);
		assertThat(films).extracting("id", "title","director").containsOnly(
				tuple(1, "Title 1","director 1"),
				tuple(2, "My Title 2","director 2"),
				tuple(3, "Third title","director 3"));
	}
	@Test
	public void shouldListFilmsByGenre() {
		List<Film>films=filmDao.listFilmsByGenre("Unknown");
		assertThat(films).isNull();
		films=filmDao.listFilmsByGenre("Comedy");
		assertThat(films).hasSize(2);
		films=filmDao.listFilmsByGenre("Drama");
		assertThat(films).hasSize(1);
		films=filmDao.listFilmsByGenre("Thriller");
		assertThat(films).isNull();
	}

	@Test
	public void shouldAddFilm() throws Exception {
		LocalDate date= LocalDate.of(2023,07,23);
		Film returnedFilm=filmDao.addFilm(new Film(1,"WarGames",date,new Genre(3,"adventure"),
				22,"John Badham","Good movie"));
		// THEN
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM film WHERE title='WarGames'");
		//assert that a row was returned
		assertThat(resultSet.next()).isTrue();
		//assert that the returned film object ID is equal to the ID generated in the database
		assertThat(resultSet.getInt("idfilm")).isEqualTo(returnedFilm.getId());
		assertThat(resultSet.getString("title")).isEqualTo("WarGames");
		assertThat(resultSet.getString("director")).isEqualTo("John Badham");
		assertThat(resultSet.getString("summary")).isEqualTo("Good movie");
		assertThat(resultSet.next()).isFalse();
		resultSet.close();
		statement.close();
		connection.close();
	}
}