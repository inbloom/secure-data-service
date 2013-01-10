
} catch (exception) {
    for(m in exception) {
        print("    > ERROR " + m  + ":" + exception[m]);
    }
}

print(database.getName() + " " +  matching.length + " Indexes matched");
print(database.getName() + " " + nonMatching.length + " Indexes did NOT match." +
"\n    " + nonMatching.join("\n    "));
