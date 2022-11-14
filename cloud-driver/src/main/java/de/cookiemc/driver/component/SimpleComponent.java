package de.cookiemc.driver.component;


import de.cookiemc.common.collection.IRandom;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.driver.component.base.SimpleTextBase;
import de.cookiemc.driver.component.event.ComponentEvent;
import de.cookiemc.driver.component.event.click.ClickAction;
import de.cookiemc.driver.component.event.click.ClickEvent;
import de.cookiemc.driver.component.event.hover.HoverAction;
import de.cookiemc.driver.component.event.hover.HoverEvent;
import de.cookiemc.driver.component.feature.tablist.SimpleTabList;
import de.cookiemc.driver.component.feature.tablist.TabList;
import de.cookiemc.driver.component.feature.title.SimpleTitle;
import de.cookiemc.driver.component.feature.title.Title;
import de.cookiemc.driver.component.feature.ComponentOption;
import de.cookiemc.driver.component.style.ComponentColor;
import de.cookiemc.driver.component.style.ComponentStyle;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SimpleComponent extends SimpleTextBase<Component> implements Component {

    /**
     * The message of this component
     */
    private String message;

    /**
     * The {@link HoverEvent} of this component if provided
     */
    private ComponentEvent<HoverEvent> hoverEvent;

    /**
     * The {@link ClickEvent} of this component if provided
     */
    private ComponentEvent<ClickEvent> clickEvent;

    /**
     * The list of other components
     */
    private List<SimpleComponent> subComponents;

    /**
     * Different states
     */
    private boolean bold, italic, strikeThrough, obfuscated, underlined;

    /**
     * Constructs an empty Component
     */
    SimpleComponent() {
        this(" ");
    }

    /**
     * Constructs a component with a message
     *
     * @param message the message
     */
    public SimpleComponent(String message) {
        this.message = message;
        this.subComponents = new ArrayList<>();

        this.bold = false;
        this.italic = false;
        this.strikeThrough = false;
        this.obfuscated = false;
        this.underlined = false;
    }

    @Override
    public Component color(ComponentColor color) {
        message = color.getColor() + message;
        return this;
    }

    public @NotNull List<Component> getSubComponents() {
        return new ArrayList<>(subComponents);
    }

    @Override
    public Component style(ComponentStyle style) {
        switch (style) {
            case BOLD:
                this.bold = true;
                break;
            case ITALIC:
                this.italic = true;
                break;
            case RANDOM:
                int random = (int) IRandom.create().nextInt(4);
                style(ComponentStyle.fromIdOptional(random).orElse(ComponentStyle.BOLD));
                break;
            case STRIKETHROUGH:
                this.strikeThrough = true;
                break;
            case UNDERLINED:
                this.underlined = true;
                break;
        }
        return this;
    }

    @Override
    public Component setContent(String content) {
        this.message = content;
        return this;
    }

    @Override
    public Component event(ComponentEvent<?> event) {
        if (event instanceof ClickEvent) {
            this.clickEvent = (ComponentEvent<ClickEvent>) event;
        } else if (event instanceof HoverEvent) {
            this.hoverEvent = (ComponentEvent<HoverEvent>) event;
        } else {
            throw new UnsupportedOperationException("Tried inserting unknown ComponentEvent of Type '" + event.getClass().getSimpleName() + "'!");
        }
        return this;
    }

    @Override
    public Component append(Component component) {
        this.subComponents.add((SimpleComponent) component);
        return this;
    }

    @Override
    public Component spacer() {
        return this.append(Component.text(" "));
    }

    @Override
    public String toString() {
        return this.message;
    }

    @Override
    public String getContent() {
        return message;
    }

    @Override
    public TabList toTabList() {
        SimpleTabList tabList = new SimpleTabList();
        ComponentOption<Component, ?> texts = options.keySet().stream().filter(option -> option.getName().equalsIgnoreCase("texts")).findFirst().orElse(null);

        if (texts != null) {
            String[] value = (String[]) texts.getValue();
            if (value != null) {
                String header = value[0];
                String footer = value[1];

                tabList.setHeader(header);
                tabList.setFooter(footer);
            }

        } else {
            tabList.setHeader("");
            tabList.setFooter("");
        }
        return tabList;
    }

    @Override
    public Title toTitle() {
        SimpleTitle title = new SimpleTitle();
        ComponentOption<Component, ?> texts = options.keySet().stream().filter(option -> option.getName().equalsIgnoreCase("texts")).findFirst().orElse(null);

        if (texts != null) {
            String[] value = (String[]) texts.getValue();
            if (value != null) {
                String mainTitle = value[0];
                String subTitle = value[1];

                title.setTitle(mainTitle);
                title.setSubTitle(subTitle);
            }

        } else {
            title.setTitle(message);
        }
        return title;
    }

    @Override
    public Component get(int index) {
        try {
            return this.subComponents.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case WRITE:

                buf.writeString(this.message);

                //Extras
                buf.writeOptional(hoverEvent, event -> buf.writeEnum(event.getType()).writeString(event.getValue()));
                buf.writeOptional(clickEvent, event -> buf.writeEnum(event.getType()).writeString(event.getValue()));
                buf.writeObjectCollection(this.subComponents);

                //Decorations
                buf.writeBoolean(bold);
                buf.writeBoolean(italic);
                buf.writeBoolean(strikeThrough);
                buf.writeBoolean(underlined);
                buf.writeBoolean(obfuscated);

                //Options
                buf.writeInt(options.size());
                for (ComponentOption<Component, ?> option : options.keySet()) {
                    buf.writeDocument(DocumentFactory.newJsonDocument(option));
                }
                break;
            case READ:

                this.message = buf.readString();

                this.hoverEvent = buf.readOptional(() -> ComponentEvent.hover(buf.readEnum(HoverAction.class), buf.readString()));
                this.clickEvent = buf.readOptional(() -> ComponentEvent.click(buf.readEnum(ClickAction.class), buf.readString()));


                //Other components
                this.subComponents = new ArrayList<>(buf.readObjectCollection(SimpleComponent.class));

                this.bold = buf.readBoolean();
                this.italic = buf.readBoolean();
                this.strikeThrough = buf.readBoolean();
                this.underlined = buf.readBoolean();
                this.obfuscated = buf.readBoolean();

                int size = buf.readInt();
                this.options = new ConcurrentHashMap<>(size);
                for (int i = 0; i < size; i++) {
                    ComponentOption<Component, ?> option = buf.readDocument().toInstance(ComponentOption.class);
                    this.options.put(option, option.getValue());
                }
                break;
        }
    }

}
