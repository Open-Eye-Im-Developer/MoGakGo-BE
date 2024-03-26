package io.oeid.mogakgo.domain.user.domain.enums;

import lombok.Getter;

@Getter
public enum DevelopLanguage {
    PYTHON("Python",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/eda9a8c6-d657-4c9b-8532-6ae3bf57ccf3"),
    JAVA("Java",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/d6c6a96c-669c-4885-aaee-5615a4459f31"),
    JAVASCRIPT("JavaScript",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/eb421c57-8d16-43c6-b506-2e14af163687"),
    C("C",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/26f006ee-71d0-415a-ba60-6a9efcaabe63"),
    CPP("C++",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/202d10e4-4bb6-4799-af90-f38fe096e708"),
    CSHARP("C#",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/c8ae1a0f-bba4-45c3-9f19-ee5dc792964a"),
    RUBY("Ruby",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/95d01e53-8f93-4e2b-9858-52b562f11eef"),
    SWIFT("Swift",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/a5034052-8bf7-4506-83ea-851753eafd7f"),
    KOTLIN("Kotlin",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/744924b4-56c4-4731-a3aa-0c6d31d33bc7"),
    GO("Go",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/e7a3bb0f-2c5d-44c9-9468-a1b837ba44ce"),
    TYPESCRIPT("TypeScript",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/17c08512-ef14-4b45-9cfc-5f774afca191"),
    SCALA("Scala",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/72cc9ff4-9b67-4a24-a307-cf11e1f1d3e5"),
    RUST("Rust",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/b3c44615-97bc-4cd4-982d-0ae1cc3987d4"),
    PHP("PHP",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/3cd5eb64-2968-4e72-8fc5-a74538ed7ad0"),
    HTML("HTML",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/6ed22629-73f6-475d-b9af-0cc45f359b90"),
    CSS("CSS",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/74b21264-84ec-42dc-95f4-a113c5e37c22"),
    ELM("Elm",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/6c518db0-1bda-4826-a505-250dbd8810e9"),
    ERLANG("Erlang",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/11b6af54-d54a-46e5-8fd9-0d482053a7bb"),
    HASKELL("Haskell",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/65e8a3b6-bdf2-45eb-a157-6b9751eadc38"),
    R("R",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/fc736684-8f19-4e2c-a809-70c029156fe2"),
    SHELL("Shell",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/00e5c53d-3ca7-4328-bc49-36b17fef7ebe"),
    SQL("SQL",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/316096d0-c508-4ea9-90a7-dd750ab17ed8"),
    DART("Dart",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/5927b6c4-b2ef-4e2d-8d07-28032975dacf"),
    OBJECT_C("Objective-C",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/c99bf59a-716f-4dfd-9414-005133ce9c97"),
    ETC("ETC",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/b489846c-bcbf-4575-8bfc-7788da4b9ba0"),
    NULL("NULL",
        "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/8f0c33f3-62ef-4379-ba6f-eb1eb0ef2471");

    private final String language;
    private final String imageUrl;

    DevelopLanguage(String language, String imageUrl) {
        this.language = language;
        this.imageUrl = imageUrl;
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
