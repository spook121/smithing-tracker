package com.smithingtracker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BarType
{
    BRONZE("Bronze", 2349),
    IRON("Iron", 2351),
    STEEL("Steel", 2353),
    MITHRIL("Mithril", 2359),
    ADAMANTITE("Adamant", 2361),
    RUNITE("Rune", 2363);

    private final String displayName;
    private final int itemId;
}
