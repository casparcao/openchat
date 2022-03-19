package top.mikecao.openchat.client.controller;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;
import top.mikecao.openchat.client.model.Emoji;
import top.mikecao.openchat.client.model.EmojiOne;
import top.mikecao.openchat.client.model.ImageCache;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author mike
 */
public class EmojiSearchController extends Parent {

	private static final boolean SHOW_MISC = false;
	@FXML
	private ScrollPane searchScrollPane;
	@FXML
	private FlowPane searchFlowPane;
	@FXML
	private TabPane tabPane;
	@FXML
	private TextField txtSearch;

	private TextArea txtArea;
	private Popup popup;

	public void area(TextArea area){
		this.txtArea = area;
	}

	public void popup(Popup popup){
		this.popup = popup;
	}

	@FXML
	void initialize() {
		if(!SHOW_MISC) {
			tabPane.getTabs().remove(tabPane.getTabs().size()-2, tabPane.getTabs().size());
		}

		searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchFlowPane.prefWidthProperty().bind(searchScrollPane.widthProperty().subtract(5));
		searchFlowPane.setHgap(5);
		searchFlowPane.setVgap(5);

		txtSearch.textProperty().addListener(x-> {
			String text = txtSearch.getText();
			int s = 2;
			if(text.isEmpty() || text.length() < s) {
				searchFlowPane.getChildren().clear();
				searchScrollPane.setVisible(false);
			} else {
				searchScrollPane.setVisible(true);
				List<Emoji> results = EmojiOne.getInstance().search(text);
				searchFlowPane.getChildren().clear();
				results.forEach(emoji ->searchFlowPane.getChildren().add(createEmojiNode(emoji)));
			}
		});

		setTabIcon();
		refreshTabs();
	}

	private void setTabIcon() {
		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			pane.setPadding(new Insets(5));
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			pane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
			pane.setHgap(5);
			pane.setVgap(5);

			tab.setId(tab.getText());
			ImageView icon = new ImageView();
			icon.setFitWidth(20);
			icon.setFitHeight(20);
			switch (tab.getText().toLowerCase()) {
				case "frequently used":
					icon.setImage(getImage(":heart:"));
					break;
				case "people":
					icon.setImage(getImage(":smiley:"));
					break;
				case "nature":
					icon.setImage(getImage(":dog:"));
					break;
				case "food":
					icon.setImage(getImage(":apple:"));
					break;
				case "activity":
					icon.setImage(getImage(":soccer:"));
					break;
				case "travel":
					icon.setImage(getImage(":airplane:"));
					break;
				case "objects":
					icon.setImage(getImage(":bulb:"));
					break;
				case "symbols":
					icon.setImage(getImage(":atom:"));
					break;
				case "flags":
					icon.setImage(getImage(":flag_eg:"));
					break;
				default:
			}

			if(icon.getImage() != null) {
				tab.setText("");
				tab.setGraphic(icon);
			}

			tab.setTooltip(new Tooltip(tab.getId()));
			tab.selectedProperty().addListener(ee-> {
				if(tab.getGraphic() == null) {
					return;
				}
				if(tab.isSelected()) {
					tab.setText(tab.getId());
				} else {
					tab.setText("");
				}
			});
		}
	}

	private Image getImage(String hex) {
		return ImageCache.getInstance().getImage(getEmojiImagePath(EmojiOne.getInstance().getEmoji(hex).getHex()));
	}

	private void refreshTabs() {
		Map<String, List<Emoji>> map = EmojiOne.getInstance().getCategorizedEmojis(1);
		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			pane.getChildren().clear();
			String category = tab.getId().toLowerCase();
			if(map.get(category) == null) {
				continue;
			}
			map.get(category).forEach(emoji -> pane.getChildren().add(createEmojiNode(emoji)));
		}
		tabPane.getSelectionModel().select(1);
	}

	private Node createEmojiNode(Emoji emoji) {
		StackPane stackPane = new StackPane();
		stackPane.setMaxSize(32, 32);
		stackPane.setPrefSize(32, 32);
		stackPane.setMinSize(32, 32);
		stackPane.setPadding(new Insets(3));
		ImageView imageView = new ImageView();
		imageView.setFitWidth(32);
		imageView.setFitHeight(32);
		imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
		stackPane.getChildren().add(imageView);

		Tooltip tooltip = new Tooltip(emoji.getShortname());
		Tooltip.install(stackPane, tooltip);
		stackPane.setCursor(Cursor.HAND);
		ScaleTransition st = new ScaleTransition(Duration.millis(90), imageView);

		stackPane.setOnMouseEntered(e-> {
			imageView.setEffect(new DropShadow());
			st.setToX(1.2);
			st.setToY(1.2);
			st.playFromStart();
			if(txtSearch.getText().isEmpty()) {
				txtSearch.setPromptText(emoji.getShortname());
			}
		});

		stackPane.setOnMouseReleased(e -> {
			txtArea.appendText(emoji.getShortname());
			txtArea.end();
			EmojiSearchController.this.popup.hide();
		});

		stackPane.setOnMouseExited(e-> {
			imageView.setEffect(null);
			st.setToX(1.);
			st.setToY(1.);
			st.playFromStart();
		});
		return stackPane;
	}

	private String getEmojiImagePath(String hexStr) {
		return Objects.requireNonNull(getClass().getResource("/emoji/image/" + hexStr + ".png")).toExternalForm();
	}

}
