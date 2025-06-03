package com.alejandroacg.choicebound.screens;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.data.LocalAdventure;
import com.alejandroacg.choicebound.data.LocalNode;
import com.alejandroacg.choicebound.data.LocalUser;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.ConditionEvaluator;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;

public class AdventureScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;
    private final LocalAdventure adventure;
    private LocalNode currentNode;
    private Image image;
    private Table contentTable;
    private ScrollPane scrollPane;
    private final String initialNodeId;
    private Image alignmentIconImage;
    private Label currentLivesLabel;

    public AdventureScreen(ChoiceboundGame game, LocalAdventure adventure, String nodeId) {
        this.game = game;
        this.adventure = adventure;
        this.initialNodeId = nodeId;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        this.backgroundColor = game.getSkin().getColor("parchment_light");
        Gdx.input.setInputProcessor(stage);
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        float screenHeight = Gdx.graphics.getHeight();
        float titleScale = 3f;

        Table header = createHeader(titleScale);
        mainTable.add(header).height(screenHeight * GameConfig.HEADER_HEIGHT_RATIO).width(Gdx.graphics.getWidth()).expandX().fillX().padTop(0).row();

        image = new Image();
        image.setScaling(Scaling.fillX);
        image.setAlign(Align.center);
        mainTable.add(image).expandX().fillX().height(screenHeight * 0.3f).padTop(0).row();

        contentTable = new Table();
        scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table scrollWrapper = new Table();
        scrollWrapper.add(scrollPane).expand().fill();

        Container<Table> parchmentContainer = new Container<>(scrollWrapper);
        parchmentContainer.setBackground(new TextureRegionDrawable(game.getResourceManager().getAtlas("ui").findRegion("container_parchment")));
        parchmentContainer.padTop(150f).padBottom(200f);

        mainTable.add(parchmentContainer).width(Gdx.graphics.getWidth() * 0.9f).padTop(20f).row();
    }

    private Table createHeader(float titleScale) {
        Table header = uiElementFactory.createHeader();

        Label titleLabel = uiElementFactory.createTitleLabel(adventure.getTitle());
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(Align.center);

        alignmentIconImage = new Image();
        alignmentIconImage.setScaling(Scaling.fit);
        alignmentIconImage.setSize(150f, 150f);
        alignmentIconImage.setAlign(Align.center);

        currentLivesLabel = new Label("", game.getSkin(), "current_lives");
        currentLivesLabel.setFontScale(1.5f);
        currentLivesLabel.setAlignment(Align.left);

        TextButton backButton = uiElementFactory.createDefaultButton(GameConfig.getString("back"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getOverlayManager().showLoadingOverlay(stage);
                game.getResourceManager().unloadAdventureArt(adventure.getUid());
                game.setScreen(new HomeScreen(game));
            }
        });

        Table statsTable = new Table();
        statsTable.add(alignmentIconImage).size(150f);
        statsTable.add(currentLivesLabel).padLeft(20f).align(Align.left);

        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.top().center();
        headerContent.add(titleLabel).padTop(20).padBottom(-20f).row();
        headerContent.add(statsTable).padBottom(5f).row();
        headerContent.add(backButton).padTop(10).padBottom(30);

        header.addActor(headerContent);
        return header;
    }

    private void loadNode(String nodeId) {
        game.getOverlayManager().showLoadingOverlay(stage);
        if (!game.getConnectivityChecker().checkConnectivityWithRedirect()) return;

        if ("last_node".equals(nodeId)) {
            LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
            loadNode(progress != null ? progress.getLastNode() : "node000");
            return;
        }

        if ("node000".equals(nodeId)) {
            LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
            if (progress != null) {
                progress.setCurrentValues(new HashMap<>());
                if (adventure.getInitialValues() != null) {
                    progress.getCurrentValues().putAll(adventure.getInitialValues());
                }
                progress.setCurrentNode("node000");
                progress.setTriggers(new HashSet<>());
                game.getUserDataManager().saveUserData(game.getLocalUser(), () -> {}, error -> game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message")));
            }
        }

        game.getOverlayManager().showLoadingOverlay(stage);
        game.getNodeDataManager().loadNode(
            adventure.getUid(), nodeId,
            node -> {
                currentNode = node;
                game.getOverlayManager().hideLoadingOverlay();
                updateImage();
                updateHeaderStats();
                updateContentTable();
            },
            error -> {
                game.getOverlayManager().hideLoadingOverlay();
                if (!"node_wip".equals(nodeId)) loadNode("node_wip");
            }
        );
    }

    private void updateImage() {
        TextureRegion imageRegion = game.getResourceManager().getAtlas(adventure.getUid() + "_art").findRegion(currentNode.getImage());
        if (imageRegion != null) {
            image.setDrawable(new TextureRegionDrawable(imageRegion));
        } else {
            image.setDrawable(null);
        }

        if (currentNode.getMusic() != null) {
            game.getMusicManager().playIfDifferent(currentNode.getMusic());
        }
    }

    private void updateHeaderStats() {
        LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
        if (progress == null) return;

        Map<String, Integer> values = progress.getCurrentValues();
        int hero = values.getOrDefault("hero", 0);
        int coward = values.getOrDefault("coward", 0);
        int killer = values.getOrDefault("killer", 0);
        int lives = values.getOrDefault("lives", 0);

        String role = (hero > coward && hero > killer) ? "hero" : (coward > killer && coward > hero) ? "coward" : (killer > hero && killer > coward) ? "killer" : "neutral";
        TextureRegion roleRegion = game.getResourceManager().getAtlas("adventure0_art").findRegion("adventure0_" + role + "_icon");

        alignmentIconImage.setDrawable(roleRegion != null ? new TextureRegionDrawable(roleRegion) : null);
        currentLivesLabel.setText(String.valueOf(lives));
    }

    private void updateContentTable() {
        Table newContentTable = new Table();
        newContentTable.setWidth(Gdx.graphics.getWidth() * 0.8f);

        Label descriptionLabel = new Label(currentNode.getText(), game.getSkin(), "roleplay_narrative_light_grey");
        descriptionLabel.setFontScale(1.1f);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);
        newContentTable.add(descriptionLabel).expandX().fillX().minHeight(100f).padBottom(40f).row();

        ArrayList<Map.Entry<String, LocalNode.LocalChoice>> sortedChoices = new ArrayList<>(currentNode.getChoices().entrySet());
        sortedChoices.sort(Map.Entry.comparingByKey());

        LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());

        for (Map.Entry<String, LocalNode.LocalChoice> entry : sortedChoices) {
            LocalNode.LocalChoice choice = entry.getValue();
            if (shouldShowChoice(choice, progress)) {
                TextButton button = createChoiceButton(choice, progress);
                newContentTable.add(button).expandX().fillX().width(Gdx.graphics.getWidth() * 0.8f).padBottom(10f).row();
            }
        }

        Gdx.app.postRunnable(() -> {
            scrollPane.setActor(newContentTable);
            contentTable = newContentTable;
        });
    }

    private boolean shouldShowChoice(LocalNode.LocalChoice choice, LocalUser.LocalProgress progress) {
        if (progress == null) return false;

        if (choice.getConditionValues() != null && !choice.getConditionValues().isEmpty()) {
            if (!ConditionEvaluator.evaluate(choice.getConditionValues(), progress.getCurrentValues())) return false;
        }

        if (choice.getConditionTriggersPositive() != null) {
            for (String trigger : choice.getConditionTriggersPositive()) {
                if (!progress.getTriggers().contains(trigger)) return false;
            }
        }

        if (choice.getConditionTriggersNegative() != null) {
            for (String trigger : choice.getConditionTriggersNegative()) {
                if (progress.getTriggers().contains(trigger)) return false;
            }
        }

        return true;
    }

    private TextButton createChoiceButton(LocalNode.LocalChoice choice, LocalUser.LocalProgress progress) {
        TextButton button = uiElementFactory.createDefaultButton(choice.getText());
        button.getLabel().setWrap(true);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.getConnectivityChecker().checkConnectivity(stage)) return;

                game.getOverlayManager().showLoadingOverlay(stage);
                String nextNodeId = choice.getNextNodeId() == null ? "node_wip" : choice.getNextNodeId();

                if (progress != null) {
                    if (choice.getModifierValues() != null) {
                        for (Map.Entry<String, Integer> mod : choice.getModifierValues().entrySet()) {
                            progress.getCurrentValues().merge(mod.getKey(), mod.getValue(), Integer::sum);
                        }
                    }
                    if (choice.getTriggerToSet() != null) progress.getTriggers().addAll(choice.getTriggerToSet());
                    if (choice.getTriggerToRemove() != null) progress.getTriggers().removeAll(choice.getTriggerToRemove());

                    if (!"last_node".equals(nextNodeId)) {
                        progress.setLastNode(progress.getCurrentNode());
                        progress.setCurrentNode(nextNodeId);
                    } else {
                        progress.setCurrentNode(progress.getLastNode());
                    }
                }

                game.getUserDataManager().saveUserData(game.getLocalUser(),
                    () -> {
                        game.getOverlayManager().hideLoadingOverlay();
                        loadNode(nextNodeId);
                    },
                    error -> {
                        game.getOverlayManager().hideLoadingOverlay();
                        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                    });
            }
        });

        return button;
    }

    @Override public void show() { loadNode(initialNodeId); }
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
