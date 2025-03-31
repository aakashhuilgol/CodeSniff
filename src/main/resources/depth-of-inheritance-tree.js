const path = require("path");

module.exports = {
    meta: {
        type: "suggestion",
        docs: {
            description: "Warn if Depth of Inheritance Tree (DIT) exceeds a given threshold",
            category: "Complexity",
            recommended: false,
        },
        schema: [
            {
                type: "integer",
                minimum: 0,
                default: 3, // Default threshold
            },
        ],
    },
    create(context) {
        const threshold = context.options[0] || 3; // Use the provided threshold or default to 3
        const classInheritance = {};

        return {
            Program(node) {
                const filename = context.getFilename();
                if (path.extname(filename) !== ".js") return;

                const classesInFile = {};

                context.getSourceCode().ast.body.forEach((statement) => {
                    if (statement.type === "ClassDeclaration" && statement.id) {
                        const className = statement.id.name;
                        const superClass = statement.superClass
                            ? statement.superClass.name
                            : null;
                        classesInFile[className] = superClass;
                    }
                });

                Object.assign(classInheritance, classesInFile);

                // Save the program node for later reporting
                this.programNode = node;
            },
            "Program:exit"() {
                const calculateDIT = (className, visited = new Set()) => {
                    if (!classInheritance[className] || visited.has(className)) return 0;
                    visited.add(className);
                    return 1 + calculateDIT(classInheritance[className], visited);
                };

                const filename = context.getFilename();
                Object.keys(classInheritance).forEach((className) => {
                    const DIT = calculateDIT(className);
                    if (DIT > threshold) {
                        context.report({
                            node: this.programNode, // Use the program node
                            message: `Class "${className}" in file "${filename}" has a Depth of Inheritance Tree (DIT) of ${DIT}, which exceeds the threshold of ${threshold}.`,
                        });
                    }
                });
            },
        };
    },
};
