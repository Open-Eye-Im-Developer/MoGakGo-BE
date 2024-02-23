package io.oeid.mogakgo.domain.user.domain.enums;

import lombok.Getter;

@Getter
public enum DevelopLanguage {
    PYTHON("Python"),
    JAVA("Java"),
    JAVASCRIPT("JavaScript"),
    C("C"),
    CPP("C++"),
    CSHARP("C#"),
    RUBY("Ruby"),
    SWIFT("Swift"),
    KOTLIN("Kotlin"),
    GO("Go"),
    TYPESCRIPT("TypeScript"),
    SCALA("Scala"),
    RUST("Rust"),
    PHP("PHP"),
    HTML("HTML"),
    CSS("CSS"),
    ELM("Elm"),
    ERLANG("Erlang"),
    HASKELL("Haskell"),
    R("R"),
    SHELL("Shell"),
    SQL("SQL"),
    DART("Dart"),
    OBJECT_C("Objective-C"),
    ETC("ETC");

    private final String language;
    DevelopLanguage(String language) {
        this.language = language;
    }

    public static DevelopLanguage of(String language) {
        for (DevelopLanguage value : values()) {
            if (value.language.equals(language)) {
                return value;
            }
        }
        return ETC;
    }
}
