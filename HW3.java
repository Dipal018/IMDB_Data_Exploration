package sample;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HW3 extends Application {

    Connection connection;
    ObservableList<String> selectedGenres;
    ObservableList<String> selectedCountries;
    ObservableList<String> selectedLocations;
    ObservableList<String> selectedTags;
    ObservableList<String> displayedGenres;
    ObservableList<String> displayedCountries;
    ObservableList<String> displayedLocations;
    ObservableList<String> displayedTags;
    SimpleBooleanProperty isAndMode;

    ListView<String> genreListView;
    ListView<String> countryListView;
    ListView<String> locationListView;

    SimpleStringProperty ratingCondition;
    SimpleStringProperty numReviewCondition;
    SimpleStringProperty tagWeightCondition;
    SimpleIntegerProperty fromReleaseYear;
    SimpleIntegerProperty toReleaseYear;
    SimpleStringProperty displayedQuery;
    SimpleStringProperty displayedResults;


    @Override
    public void init() {
        if (!DatabaseHelper.loadDriver()) {
            return;
        }
        connection = DatabaseHelper.connectToDatabase();
        if (connection == null) {
            return;
        }
        selectedGenres = FXCollections.observableArrayList();
        selectedCountries = FXCollections.observableArrayList();
        selectedLocations = FXCollections.observableArrayList();
        selectedTags = FXCollections.observableArrayList();
        displayedGenres = FXCollections.observableArrayList();
        displayedCountries = FXCollections.observableArrayList();
        displayedLocations = FXCollections.observableArrayList();
        displayedTags = FXCollections.observableArrayList();
        numReviewCondition = new SimpleStringProperty("");
        ratingCondition = new SimpleStringProperty("");
        tagWeightCondition = new SimpleStringProperty("");
        fromReleaseYear = new SimpleIntegerProperty();
        toReleaseYear = new SimpleIntegerProperty();
        displayedQuery = new SimpleStringProperty("");
        displayedResults = new SimpleStringProperty("");
        displayedLocations.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                selectedLocations.clear();
            }
        });
        displayedCountries.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                selectedCountries.clear();
                displayedLocations.clear();
            }
        });
        displayedGenres.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                selectedGenres.clear();
            }
        });
        isAndMode = new SimpleBooleanProperty(false);
        displayedQuery.addListener((observable, oldValue, newValue) -> displayResultsAsync());
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        GridPane mainGrid = new GridPane();
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(0, 10, 0, 10));

        GridPane grid1 = new GridPane();
        grid1.setHgap(10);
        grid1.setVgap(10);
        Text genresTitle = new Text("Genres");
        Text contriesTitle = new Text("Country");
        Text locationsTitle = new Text("Filming Location Country");
        Text criticsRating = new Text("Critic's Rating");


        grid1.addRow(1, genresTitle, contriesTitle, locationsTitle);

        ScrollPane genresScrollPane = new ScrollPane();
        genresScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        genresScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        genresScrollPane.setMaxHeight(200);
        genresScrollPane.setPrefSize(140, 200);

        genreListView = new ListView<>();
        genreListView.setItems(displayedGenres);
        genresScrollPane.setContent(genreListView);

        ScrollPane countriesScrollPane = new ScrollPane();
        countriesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        countriesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        countriesScrollPane.setMaxHeight(200);
        countriesScrollPane.setPrefSize(140, 200);

        countryListView = new ListView<>();
        countryListView.setItems(displayedCountries);
        countriesScrollPane.setContent(countryListView);

        ScrollPane locationsScrollPane = new ScrollPane();
        locationsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        locationsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        locationsScrollPane.setMaxHeight(200);
        locationsScrollPane.setPrefSize(140, 200);

        locationListView = new ListView<>();
        locationListView.setItems(displayedLocations);
        locationsScrollPane.setContent(locationListView);

        grid1.addRow(2, genresScrollPane, countriesScrollPane, locationsScrollPane);

        final ToggleGroup group = new ToggleGroup();
        RadioButton rband = new RadioButton("AND");
        RadioButton rbor = new RadioButton("OR");
        rbor.setToggleGroup(group);
        rband.setToggleGroup(group);
        rbor.setSelected(true);
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            isAndMode.set(group.getSelectedToggle() == rband);
            displayedCountries.clear();
            displayedLocations.clear();
            displayedTags.clear();
            displayedQuery.set("");
            displayedResults.set("");
            refreshCountryAsync();
            refreshMovieTagsAsync();
            refreshQuery();
        });
        grid1.addRow(3,rband, rbor);
        GridPane grid2 = new GridPane();
        grid2.setHgap(10);
        grid2.setVgap(10);
        Text ratingTitle = new Text("Critic's Rating");
        Text tagValueTitle = new Text("Movie Tag Values");

        Text ratingText = new Text("Rating");
        Text ratingValueText = new Text("Value");
        Text numReviewsText = new Text("Num. of Reviews");
        Text reviewValueText = new Text("Value");

        ObservableList<String> ratingOptions =
                FXCollections.observableArrayList(
                        "=",
                        "<",
                        ">",
                        ">=",
                        "<="
                );
        ComboBox ratingComboBox = new ComboBox(ratingOptions);
        ratingComboBox.getSelectionModel().selectFirst();
        ObservableList<String> reviewOptions =
                FXCollections.observableArrayList(
                        "=",
                        "<",
                        ">",
                        ">=",
                        "<="
                );
        ComboBox reviewComboBox = new ComboBox(reviewOptions);
        reviewComboBox.getSelectionModel().selectFirst();

        TextField ratingValue = new TextField();
        ratingValue.setMaxWidth(70);
        TextField reviewValue = new TextField();
        reviewValue.setMaxWidth(70);
        GridPane criticRatingGrid = new GridPane();
        criticRatingGrid.setPadding(new Insets(10, 10, 10, 10));
        criticRatingGrid.setHgap(10);
        criticRatingGrid.setVgap(10);

        criticRatingGrid.addRow(1,ratingText,ratingComboBox);
        criticRatingGrid.addRow(2,ratingValueText,ratingValue);
        criticRatingGrid.addRow(3,numReviewsText, reviewComboBox);
        criticRatingGrid.addRow(4,reviewValueText, reviewValue);

        ScrollPane showQueryScrollPane = new ScrollPane();
        showQueryScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        showQueryScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        showQueryScrollPane.setMaxHeight(280);
        showQueryScrollPane.setPrefSize(1400, 280);

        TextArea showQueryText = new TextArea();
        showQueryText.setPrefSize(1400, 280);
        showQueryText.setWrapText(true);
        showQueryText.textProperty().bind(displayedQuery);
        showQueryText.setEditable(false);
        showQueryScrollPane.setContent(showQueryText);

        ScrollPane showResultScrollPane = new ScrollPane();
        showResultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        showResultScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        showResultScrollPane.setMaxHeight(280);
        showResultScrollPane.setPrefSize(1400, 280);

        Text showResultsText = new Text();
        showResultsText.textProperty().bind(displayedResults);
        showResultScrollPane.setContent(showResultsText);

        showResultScrollPane.setPrefSize(1400, 200);
        mainGrid.add(showQueryScrollPane, 1, 2);
        mainGrid.add(showResultScrollPane, 2, 2);



        //Text criticsRating = new Text("Critic's Rating");


//        grid1.addRow(1, genresTitle, contriesTitle, locationsTitle);


        ratingComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(ratingValue.getText().isEmpty()) {
                    ratingCondition.set("");
                } else {
                    String operator = ratingComboBox.getSelectionModel().getSelectedItem().toString();
                    ratingCondition.set(operator + " " + ratingValue.getText());
                }
                refreshMovieTagsAsync();
                refreshQuery();
            }
        });
        ratingValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()) {
                ratingCondition.set("");
            } else {
                String operator = ratingComboBox.getSelectionModel().getSelectedItem().toString();
                ratingCondition.set(operator + " " + newValue);
            }
            refreshMovieTagsAsync();
            refreshQuery();
        });

        reviewComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(reviewValue.getText().isEmpty()) {
                    numReviewCondition.set("");
                } else {
                    String operator = reviewComboBox.getSelectionModel().getSelectedItem().toString();
                    numReviewCondition.set(operator + " " + reviewValue.getText());
                }
                refreshMovieTagsAsync();
                refreshQuery();
            }
        });
        reviewValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()) {
                numReviewCondition.set("");
            } else {
                String operator = reviewComboBox.getSelectionModel().getSelectedItem().toString();
                numReviewCondition.set(operator + " " + newValue);
            }
            refreshMovieTagsAsync();
            refreshQuery();
        });

        Text movieYearTitle = new Text("Movie Year");
        Text movieYearFromTitle = new Text("From");
        Text movieYearToTitle = new Text("To");
        DatePicker fromMovieYear = new DatePicker(LocalDate.now().minusYears(150));
        DatePicker toMovieYear = new DatePicker(LocalDate.now());
        fromMovieYear.setOnAction(event -> {
            fromReleaseYear.set(fromMovieYear.getValue().getYear());
            refreshMovieTagsAsync();
            refreshQuery();
        });
        toMovieYear.setOnAction(event -> {
            toReleaseYear.set(toMovieYear.getValue().getYear());
            refreshMovieTagsAsync();
            refreshQuery();
        });
        fromReleaseYear.set(LocalDate.now().minusYears(150).getYear());
        toReleaseYear.set(LocalDate.now().getYear());

        criticRatingGrid.addRow(5, movieYearTitle);
        criticRatingGrid.addRow(6, movieYearFromTitle, fromMovieYear);
        criticRatingGrid.addRow(7, movieYearToTitle, toMovieYear);

        GridPane tagsGridPane = new GridPane();
        tagsGridPane.setPadding(new Insets(10, 10, 10, 10));
        tagsGridPane.setHgap(10);
        tagsGridPane.setVgap(10);

        ScrollPane tagsScrollPane = new ScrollPane();
        ListView<String> movieTags = new ListView<>();
        movieTags.setItems(displayedTags);
        tagsScrollPane.setContent(movieTags);
        tagsScrollPane.setMaxHeight(140);
        tagsScrollPane.setPrefSize(200, 140);
        tagsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tagsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tagsGridPane.add(tagsScrollPane, 1, 1, 2, 1);

        Text tagWeightTitle = new Text("Tag Weight");
        Text tagWeightValueTitle = new Text("Value");
        ObservableList<String> tagWeightOptions =
                FXCollections.observableArrayList(
                        "=",
                        "<",
                        ">"
                );
        ComboBox<String> tagWeightOperationComboBox = new ComboBox<String>(tagWeightOptions);
        tagWeightOperationComboBox.getSelectionModel().selectFirst();
        TextField tagWeightValue = new TextField();

        tagWeightOperationComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(tagWeightValue.getText().isEmpty()) {
                    tagWeightCondition.set("");
                } else {
                    String operator = tagWeightOperationComboBox.getSelectionModel().getSelectedItem().toString();
                    tagWeightCondition.set(operator + " " + tagWeightValue.getText());
                }
                refreshQuery();
            }
        });
        tagWeightValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()) {
                tagWeightCondition.set("");
            } else {
                String operator = tagWeightOperationComboBox.getSelectionModel().getSelectedItem().toString();
                tagWeightCondition.set(operator + " " + newValue);
            }
            refreshQuery();
        });

        tagsGridPane.add(tagWeightTitle, 1,2);
        tagsGridPane.add(tagWeightOperationComboBox, 2,2);
        tagsGridPane.add(tagWeightValueTitle, 1,3);
        tagsGridPane.add(tagWeightValue, 2,3);

        grid2.add(ratingTitle, 1, 1);
        grid2.add(tagValueTitle, 2, 1);
        grid2.add(criticRatingGrid,1,2);
        grid2.add(tagsGridPane, 2,2);



        mainGrid.add(grid1, 1,1);
        mainGrid.add(grid2, 2,1);

        primaryStage.setTitle("MOVIE");
        primaryStage.setScene(new Scene(mainGrid, 900, 600));
        primaryStage.show();

        genreListView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                    if (isNowSelected) {
                        selectedGenres.addAll(item);
                    } else {
                        selectedGenres.removeAll(item);
                    }
                });
                return observable ;
            }
        }));
        countryListView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                    if (isNowSelected) {
                        selectedCountries.add(item);
                    } else {
                        selectedCountries.remove(item);
                    }
                });
                return observable;
            }
        }));
        locationListView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                    if (isNowSelected) {
                        selectedLocations.add(item);
                    } else {
                        selectedLocations.remove(item);
                    }
                });
                return observable;
            }
        }));
        movieTags.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                    if (isNowSelected) {
                        selectedTags.add(item);
                    } else {
                        selectedTags.remove(item);
                    }
                });
                return observable ;
            }
        }));

        refreshGenresAsync();

        selectedGenres.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                displayedCountries.clear();
                refreshCountryAsync();
                refreshMovieTagsAsync();
                refreshQuery();
            }
        });
        selectedCountries.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                displayedLocations.clear();
                refreshLocationsAsync();
                refreshMovieTagsAsync();
                refreshQuery();
            }
        });
        selectedLocations.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                displayedTags.clear();
                refreshMovieTagsAsync();
                refreshQuery();
            }
        });
        selectedTags.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                refreshQuery();
            }
        });
    }

    private void refreshGenresAsync() {
        Task<ObservableList<String>> fetchGenres = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                Statement lookupGenres = connection.createStatement();
                String sql = "SELECT GENRES FROM MOVIE_GENRES GROUP BY GENRES ORDER BY GENRES";
                ResultSet resultSet = lookupGenres.executeQuery(sql);
                ObservableList<String> genres = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    String genre = resultSet.getString("GENRES");
                    genres.add(genre);
                }
                return genres;
            }
        };
        fetchGenres.setOnSucceeded(event -> {
            selectedGenres.clear();
            displayedGenres.clear();
            displayedGenres.addAll(fetchGenres.getValue());
        });
        Thread thread = new Thread(fetchGenres);
        thread.setDaemon(true);
        thread.run();
    }

    private void refreshCountryAsync() {
        Task<ObservableList<String>> fetchCountries = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                Statement lookupCountries = connection.createStatement();
                if (selectedGenres.size() == 0) {
                    selectedCountries.clear();
                    selectedLocations.clear();
                    return FXCollections.emptyObservableList();
                }

                List<String> quotedGenres = new ArrayList<>();
                for (String s : selectedGenres) {
                    quotedGenres.add("'" + s.replace("'", "''") + "'");
                }
                String sql = null;
                if (isAndMode.get()) {
                    sql = "SELECT COUNTRY "
                            + "FROM COUNTRIES "
                            + "WHERE MOVIE_ID IN ("
                            + "  SELECT MOVIE_ID "
                            + "  FROM ("
                            + "    SELECT MOVIE_ID, COUNT(1) as no_of_genres"
                            + "    FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "      " + String.join(",", quotedGenres) + ")"
                            + "    GROUP BY MOVIE_ID"
                            + "  )"
                            + "  WHERE no_of_genres = " + selectedGenres.size()
                            + ")"
                            + " GROUP BY COUNTRY"
                            + " ORDER BY COUNTRY";
                } else {
                    sql = "SELECT COUNTRY "
                            + "FROM COUNTRIES "
                            + "WHERE MOVIE_ID IN ("
                            + "  SELECT MOVIE_ID FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "    " + String.join(",", quotedGenres) + "))"
                            + " GROUP BY COUNTRY"
                            + " ORDER BY COUNTRY";
                }
                System.out.println("Country sql: " + sql);
                ResultSet resultSet = lookupCountries.executeQuery(sql);
                ObservableList<String> countries = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    String genre = resultSet.getString("COUNTRY");
                    countries.add(genre);
                }
                return countries;
            }
        };
        fetchCountries.setOnSucceeded(event -> {
            selectedCountries.clear();
            displayedCountries.clear();
            System.out.println("New list of countries: " + fetchCountries.getValue());
            displayedCountries.addAll(fetchCountries.getValue());
        });
        Thread thread = new Thread(fetchCountries);
        thread.setDaemon(true);
        thread.run();
    }

    private void refreshLocationsAsync() {
        Task<ObservableList<String>> fetchLocations = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                Statement lookupLocations = connection.createStatement();
                if (selectedCountries.size() == 0) {
                    selectedLocations.clear();
                    return FXCollections.emptyObservableList();
                }

                List<String> quotedGenres = new ArrayList<>();
                for (String s : selectedGenres) {
                    quotedGenres.add("'" + s.replace("'", "''") + "'");
                }
                List<String> quotedCountries = new ArrayList<>();
                for (String s : selectedCountries) {
                    quotedCountries.add("'" + s.replace("'", "''") + "'");
                }
                String sql = null;
                if (isAndMode.get()) {
                    if (selectedCountries.size() != 1) {
                        return FXCollections.emptyObservableList();
                    }
                    String selectedCountry = quotedCountries.get(0);
                    sql = "SELECT c.LOCATION1 as location1 "
                            + "FROM ("
                            + "  SELECT MOVIE_ID "
                            + "  FROM ("
                            + "    SELECT MOVIE_ID, COUNT(1) as no_of_genres"
                            + "    FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "      " + String.join(",", quotedGenres) + ")"
                            + "    GROUP BY MOVIE_ID"
                            + "  )"
                            + "  WHERE no_of_genres = " + selectedGenres.size()
                            + ") a JOIN COUNTRIES b"
                            + " ON a.MOVIE_ID = b.MOVIE_ID"
                            + " JOIN LOCATIONS c"
                            + " ON b.MOVIE_ID = c.MOVIE_ID"
                            + " WHERE b.COUNTRY = " + selectedCountry
                            + " GROUP BY location1"
                            + " ORDER BY location1";
                } else {
                    sql = "SELECT LOCATION1 "
                            + "FROM LOCATIONS "
                            + "WHERE MOVIE_ID IN ("
                            + "  (SELECT MOVIE_ID FROM COUNTRIES WHERE COUNTRY IN ("
                            + "    " + String.join(",", quotedCountries) + "))"
                            + "  INTERSECT "
                            + "  (SELECT MOVIE_ID FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "    " + String.join(",", quotedGenres)+"))"
                            + ")"
                            + " GROUP BY LOCATION1"
                            + " ORDER BY LOCATION1";
                }

                System.out.println("Location sql: " + sql);
                ResultSet resultSet = lookupLocations.executeQuery(sql);
                ObservableList<String> locations = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    String genre = resultSet.getString("LOCATION1");
                    locations.add(genre);
                }
                return locations;
            }
        };
        fetchLocations.setOnSucceeded(event -> {
            selectedLocations.clear();
            displayedLocations.clear();
            displayedLocations.addAll(fetchLocations.getValue());
        });
        Thread thread = new Thread(fetchLocations);
        thread.setDaemon(true);
        thread.run();
    }

    private void refreshMovieTagsAsync() {
        Task<ObservableList<String>> fetchTags = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                if (selectedGenres.isEmpty()) {
                    return FXCollections.emptyObservableList();
                }
                List<String> quotedGenres = new ArrayList<>();
                for (String s : selectedGenres) {
                    quotedGenres.add("'" + s.replace("'", "''") + "'");
                }
                List<String> quotedCountries = new ArrayList<>();
                for (String s : selectedCountries) {
                    quotedCountries.add("'" + s.replace("'", "''") + "'");
                }
                String optionalCriticRatingWhereClause = "WHERE m.RELEASE_YEAR >= " + fromReleaseYear.get()
                        + " AND m.RELEASE_YEAR <= " + toReleaseYear.get();
                if (!ratingCondition.get().isEmpty() || !numReviewCondition.get().isEmpty()) {
                    if (!ratingCondition.get().isEmpty()) {
                        optionalCriticRatingWhereClause += " AND  m.RTALLCRITICSRATING " + ratingCondition.get();
                    }

                    if (!numReviewCondition.get().isEmpty()) {
                        optionalCriticRatingWhereClause += " AND m.RTALLCRITICSNUMREVIEWS " + numReviewCondition.get();
                    }
                }
                String sql = null;
                if (isAndMode.get()) {
                    if (selectedCountries.size() > 1) {
                        selectedLocations.clear();
                        return FXCollections.emptyObservableList();
                    }
                    String optionalCountryLocationClause = "";
                    if (selectedCountries.size() == 1 && selectedLocations.size() > 0) {
                        optionalCountryLocationClause = "WHERE b.COUNTRY = " + quotedCountries.get(0)
                                + "   AND c.LOCATION1 in (" + convertToCommaSeparatedList(selectedLocations) + ") ";
                    } else if (quotedCountries.size() == 0 && selectedLocations.size() > 0) {
                        optionalCountryLocationClause = "WHERE c.LOCATION1 in (" + convertToCommaSeparatedList(selectedLocations) + ") ";
                    } else if (quotedCountries.size() == 1 && selectedLocations.size() == 0) {
                        optionalCountryLocationClause = "WHERE b.COUNTRY = " + convertToCommaSeparatedList(selectedCountries);
                    }

                    String optionalNumLocationsClause = "";
                    if (selectedLocations.size() > 0) {
                        optionalNumLocationsClause = " WHERE num_locations = " + selectedLocations.size();
                    }
                    String movieIdFromGenresCountryLocationSql =
                            "SELECT MOVIE_ID FROM ( "
                            + "SELECT c.MOVIE_ID as MOVIE_ID, COUNT(DISTINCT c.LOCATION1) as num_locations "
                            + "FROM ("
                            + "  SELECT MOVIE_ID "
                            + "  FROM ("
                            + "    SELECT MOVIE_ID, COUNT(1) as no_of_genres"
                            + "    FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "      " + String.join(",", quotedGenres) + ")"
                            + "    GROUP BY MOVIE_ID"
                            + "  )"
                            + "  WHERE no_of_genres = " + selectedGenres.size()
                            + ") a JOIN COUNTRIES b "
                            + " ON a.MOVIE_ID = b.MOVIE_ID "
                            + " JOIN LOCATIONS c "
                            + " ON b.MOVIE_ID = c.MOVIE_ID "
                            + optionalCountryLocationClause
                            + " GROUP BY c.MOVIE_ID "
                            + " ORDER BY c.MOVIE_ID "
                            + ") " + optionalNumLocationsClause;
                    String movieJoinSql = "SELECT m.MOVIE_ID "
                            + "FROM MOVIE m JOIN "
                            + " (" + movieIdFromGenresCountryLocationSql + ") n "
                            + "ON m.MOVIE_ID = n.MOVIE_ID "
                            + optionalCriticRatingWhereClause;
                    sql = "SELECT t.TAG_TEXT as TAG_TEXT "
                            + "FROM (" + movieJoinSql + " ) o "
                            + "JOIN MOVIE_TAGS mt "
                            + "ON o.MOVIE_ID = mt.MOVIE_ID "
                            + "JOIN TAGS t "
                            + "ON t.TAG_ID = mt.TAG_ID "
                            + "GROUP BY TAG_TEXT "
                            + "ORDER BY TAG_TEXT";
                } else {
                    sql = "SELECT TAG_TEXT "
                            + "FROM ("
                            + "  (SELECT MOVIE_ID FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "    " + String.join(",", quotedGenres) + "))"
                            + (quotedCountries.size() > 0 ?
                                ("  INTERSECT "
                                 + "  (SELECT MOVIE_ID FROM COUNTRIES WHERE COUNTRY IN ("
                                 + "    " + String.join(",", quotedCountries) + "))")
                                : "")
                            + (selectedLocations.size() > 0 ?
                                ("  INTERSECT "
                                 + "  (SELECT MOVIE_ID FROM LOCATIONS WHERE LOCATION1 IN ("
                                 + "    " + convertToCommaSeparatedList(selectedLocations) + "))")
                                 : "")
                            + " ) selectedMovies"
                            + " JOIN MOVIE_TAGS mt"
                            + " ON mt.MOVIE_ID = selectedMovies.MOVIE_ID"
                            + " JOIN TAGS t"
                            + " ON t.TAG_ID = mt.TAG_ID"
                            + " JOIN MOVIE m "
                            + " ON mt.MOVIE_ID = m.MOVIE_ID "
                            + optionalCriticRatingWhereClause
                            + " GROUP BY TAG_TEXT"
                            + " ORDER BY TAG_TEXT";
                }

                System.out.println("Tag sql: " + sql);
                Statement lookupTags = connection.createStatement();
                ResultSet resultSet = lookupTags.executeQuery(sql);
                ObservableList<String> tagTexts = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    String tagText = resultSet.getString("TAG_TEXT");
                    tagTexts.add(tagText);
                }
                return tagTexts;
            }
        };
        fetchTags.setOnSucceeded(event -> {
            selectedTags.clear();
            displayedTags.clear();
            displayedTags.addAll(fetchTags.getValue());
        });
        Thread thread = new Thread(fetchTags);
        thread.setDaemon(true);
        thread.run();
    }

    private void refreshQuery() {
        String sql = "";
        if (isAndMode.get()) {
            String optionalCriticRatingWhereClause = "WHERE m.RELEASE_YEAR >= " + fromReleaseYear.get()
                    + " AND m.RELEASE_YEAR <= " + toReleaseYear.get();
            if (!ratingCondition.get().isEmpty() || !numReviewCondition.get().isEmpty()) {
                if (!ratingCondition.get().isEmpty()) {
                    optionalCriticRatingWhereClause += " AND  m.RTALLCRITICSRATING " + ratingCondition.get();
                }

                if (!numReviewCondition.get().isEmpty()) {
                    optionalCriticRatingWhereClause += " AND m.RTALLCRITICSNUMREVIEWS " + numReviewCondition.get();
                }
            }
            String optionalCountryLocationClause = "";
            if (selectedCountries.size() == 1 && selectedLocations.size() > 0) {
                optionalCountryLocationClause = "WHERE b.COUNTRY = '" + selectedCountries.get(0)
                        + "'   AND c.LOCATION1 in (" + convertToCommaSeparatedList(selectedLocations) + ") ";
            } else if (selectedCountries.size() == 0 && selectedLocations.size() > 0) {
                optionalCountryLocationClause = "WHERE c.LOCATION1 in (" + convertToCommaSeparatedList(selectedLocations) + ") ";
            } else if (selectedCountries.size() == 1 && selectedLocations.size() == 0) {
                optionalCountryLocationClause = "WHERE b.COUNTRY = " + convertToCommaSeparatedList(selectedCountries);
            } else if (selectedCountries.size() > 1) {
                optionalCountryLocationClause = "WHERE b.COUNTRY = '" + String.join(",", selectedCountries) + "'";
            }

            String optionalNumLocationsClause = "";
            if (selectedLocations.size() > 0) {
                optionalNumLocationsClause = " WHERE num_locations = " + selectedLocations.size();
            }
            String movieIdFromGenresCountryLocationSql =
                    "SELECT MOVIE_ID FROM ( "
                            + "SELECT c.MOVIE_ID as MOVIE_ID, COUNT(DISTINCT c.LOCATION1) as num_locations "
                            + "FROM ("
                            + "  SELECT MOVIE_ID "
                            + "  FROM ("
                            + "    SELECT MOVIE_ID, COUNT(1) as no_of_genres"
                            + "    FROM MOVIE_GENRES WHERE GENRES IN ("
                            + "      " + convertToCommaSeparatedList(selectedGenres) + ")"
                            + "    GROUP BY MOVIE_ID"
                            + "  )"
                            + "  WHERE no_of_genres = " + selectedGenres.size()
                            + ") a JOIN COUNTRIES b "
                            + " ON a.MOVIE_ID = b.MOVIE_ID "
                            + " JOIN LOCATIONS c "
                            + " ON b.MOVIE_ID = c.MOVIE_ID "
                            + optionalCountryLocationClause
                            + " GROUP BY c.MOVIE_ID "
                            + " ORDER BY c.MOVIE_ID "
                            + ") " + optionalNumLocationsClause;
            String movieJoinSql = "SELECT m.MOVIE_ID "
                    + "FROM MOVIE m JOIN "
                    + " (" + movieIdFromGenresCountryLocationSql + ") n "
                    + "ON m.MOVIE_ID = n.MOVIE_ID "
                    + optionalCriticRatingWhereClause;
            sql = movieJoinSql;
            if (selectedTags.size() > 0 || !tagWeightCondition.get().isEmpty()) {
                String optionalTagCondition = "";
                if (!tagWeightCondition.get().isEmpty()) {
                    optionalTagCondition += " mt.TAG_WEIGHT " + tagWeightCondition.get();
                }
                if (selectedTags.size() > 0) {
                    if (!optionalTagCondition.isEmpty()) {
                        optionalTagCondition += " AND ";
                    }
                    optionalTagCondition += " t.TAG_TEXT IN (" + convertToCommaSeparatedList(selectedTags) + ")";
                }
                sql = "SELECT o.MOVIE_ID, COUNT(DISTINCT t.TAG_TEXT) as NUM_TAG_TEXTS "
                        + "FROM (" + sql + " ) o "
                        + "JOIN MOVIE_TAGS mt "
                        + "ON o.MOVIE_ID = mt.MOVIE_ID "
                        + "JOIN TAGS t "
                        + "ON t.TAG_ID = mt.TAG_ID "
                        + " WHERE " + optionalTagCondition
                        + "GROUP BY o.MOVIE_ID ";
                sql = "SELECT MOVIE_ID FROM ( " + sql + " ) WHERE NUM_TAG_TEXTS = " + selectedTags.size();
            }
        } else {
            sql = "SELECT MOVIE_ID FROM MOVIE_GENRES WHERE GENRES IN (" + convertToCommaSeparatedList(selectedGenres) + ")";
            if (selectedCountries.size() > 0) {
                String optionalCountryAttribute = selectedLocations.isEmpty() ? "" : ", COUNTRY ";
                sql = "SELECT MOVIE_ID " + optionalCountryAttribute + " FROM COUNTRIES WHERE COUNTRY IN (" + convertToCommaSeparatedList(selectedCountries) + ") AND MOVIE_ID IN ( " + sql + " )";
            }
            if (selectedLocations.size() > 0) {
                sql = "SELECT a.MOVIE_ID FROM ( " + sql + " ) a JOIN LOCATIONS b ON a.MOVIE_ID = b.MOVIE_ID WHERE b.LOCATION1 in ( " + convertToCommaSeparatedList(selectedLocations) + " ) GROUP BY a.MOVIE_ID";
            }
            sql = "SELECT c.MOVIE_ID FROM ( " + sql + " ) c JOIN MOVIE d ON c.MOVIE_ID = d.MOVIE_ID WHERE d.RELEASE_YEAR >= " + fromReleaseYear.get() + " AND d.RELEASE_YEAR <= " + toReleaseYear.get();
            if (!ratingCondition.get().isEmpty()) {
                sql = sql + " AND d.RTALLCRITICSRATING " + ratingCondition.get();
            }
            if (!numReviewCondition.get().isEmpty()) {
                sql = sql + " AND d.RTALLCRITICSNUMREVIEWS " + numReviewCondition.get();
            }
            if (!selectedTags.isEmpty() || !tagWeightCondition.get().isEmpty()) {
                String selectedTagsCond = "";
                if (!selectedTags.isEmpty()) {
                    selectedTagsCond = " g.TAG_TEXT in ( " + convertToCommaSeparatedList(selectedTags) + " ) ";
                }
                String selectedTagWeightCond = "";
                if (!tagWeightCondition.get().isEmpty()) {
                    selectedTagWeightCond = " f.TAG_WEIGHT " + tagWeightCondition.get();
                }
                String tagWhereCond = selectedTagsCond;
                if (!tagWhereCond.isEmpty() && !selectedTagWeightCond.isEmpty()) {
                    tagWhereCond = tagWhereCond + " AND ";
                }
                tagWhereCond += selectedTagWeightCond;
                sql = "SELECT e.MOVIE_ID FROM ( " + sql + " ) e JOIN MOVIE_TAGS f ON e.MOVIE_ID = f.MOVIE_ID JOIN TAGS g ON f.TAG_ID = g.TAG_ID WHERE " + tagWhereCond ;
            }
        }
        sql = "SELECT DISTINCT MOVIE_ID FROM ( " + sql + " )";
        // SQL will return selected MOVIE_IDs.. join with other tables to print necessary values.
        String avgRatings = "(h.RTALLCRITICSRATING + h.RTTOPCRITICSRATING + h.RTAUDIENCERATING) / 3 as AVG_RATING";
        String numReviews = "(h.RTALLCRITICSNUMREVIEWS + h.RTTOPCRITICSNUMREVIEWS + h.RTAUDIENCENUMRATINGS ) / 3 AS AVG_NUM_REVIEWS";
        String allLocations = "SELECT MOVIE_ID, LISTAGG(LOCATION1, ', ') WITHIN GROUP (ORDER BY LOCATION1) as ALL_LOCATIONS FROM (SELECT DISTINCT MOVIE_ID, LOCATION1 FROM LOCATIONS) GROUP BY MOVIE_ID";
        sql = "SELECT h.MOVIE_ID, h.MOVIE_TITLE, h.RELEASE_YEAR, " + avgRatings + " , " + numReviews + ", j.COUNTRY, k.ALL_LOCATIONS  FROM MOVIE h JOIN (" + sql + " ) i ON h.MOVIE_ID = i.MOVIE_ID JOIN COUNTRIES j ON h.MOVIE_ID = j.MOVIE_ID JOIN (" + allLocations + ") k ON h.MOVIE_ID = k.MOVIE_ID";
        System.out.println("FINAL SQL = " + sql);
        displayedQuery.set(new SqlFormatter().format(sql));
    }

    private void displayResultsAsync() {
        if (displayedQuery.get().isEmpty()) {
            displayedResults.set("");
            return;
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(displayedQuery.get());
            StringBuilder results = new StringBuilder();
            while (resultSet.next()) {
                results.append("\n ")
                        .append(resultSet.getInt("MOVIE_ID")).append(" : ")
                        .append(resultSet.getString("MOVIE_TITLE")).append(" : ")
                        .append(resultSet.getInt("RELEASE_YEAR")).append(" : ")
                        .append(resultSet.getString("COUNTRY")).append(" : ")
                        .append(String.format("%.2f", resultSet.getDouble("AVG_RATING"))).append(" : ")
                        .append(String.format("%.2f", resultSet.getDouble("AVG_NUM_REVIEWS"))).append(" : ")
                        .append(resultSet.getString("ALL_LOCATIONS"));
            }
            displayedResults.set(results.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String convertToCommaSeparatedList(List<String> list) {
        return list.stream().map(s -> "'" + s.replace("'", "''") + "'").collect(Collectors.joining(","));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
