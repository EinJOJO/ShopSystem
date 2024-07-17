rootProject.name = "ShopSystem"
include("core")
include("platforms")
include("platforms:common")
findProject(":platforms:common")?.name = "common"
include("platforms:akani")
findProject(":platforms:akani")?.name = "akani"
