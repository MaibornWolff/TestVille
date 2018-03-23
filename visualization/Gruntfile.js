"use strict";

var path = require("path");

module.exports = function (grunt) {

    // Load grunt tasks automatically
    require("load-grunt-tasks")(grunt);

    // Time how long tasks take
    require("time-grunt")(grunt);

    // Define configuration for all tasks
    grunt.initConfig({

        pkg: grunt.file.readJSON('package.json'),

        mocha_istanbul: {
            coverage: {
                src: ["test/unit-helper.js", "app/**/*.spec.js"],
                options: {
                    mask: '**/*.js',
                    require: ['test/mocha-babel'],
                    coverageFolder: 'dist/coverage',
                    mochaOptions: ['--compilers', 'js:babel-core/register'],
                    istanbulOptions: ['--handle-sigint']
                }
            }
        },

        nwjs: {
            options: {
                platforms: ['win','osx64', 'linux'],
                buildDir: './dist/packages/',
                buildType: 'default',
                cacheDir: '.cache'
            },
            src: ['./dist/app/**/*', './package.json', './LICENSE.md']
        },

        browserify: {
            dist: {
                options: {
                    transform: ["babelify", "es6-arrow-function"],
                    presets: ["es2015"],
                    browserifyOptions: {
                        debug:true
                    }
                },
                files: {
                    "dist/app/libs/bundle.js": "app/app.js"
                }
            }
        },

        htmlmin: {
            dist: {
                options: {
                    removeComments: true,
                    collapseWhitespace: true
                },
                files: [{
                    "expand": true,
                    "cwd": "app/",
                    "src": ["**/*.html"],
                    "dest": "dist/app/",
                    "ext": ".html",
                    "flatten": true
                }]
            }
        },

        jshint: {
            options: {
                jshintrc: ".jshintrc",
                reporter: require("jshint-stylish")
            },
            all: {
                src: [
                    "app/**/*.js","!app/**/*.spec.js"
                ]
            }
        },

        concat: {
            css: {
                src: "dist/app/style/**/*.css",
                dest: "dist/app/css/style.css"
            }
        },

        connect: {
            server: {
                keepalive: true,
                options: {
                    port: 9000,
                    base: ''
                }
            }
        },

        copy: {
            license: {
                files: [
                    {expand: false, src: ["LICENSE.md"], dest: "dist/app/LICENSE.md"}
                ]
            },
            jquery: {
                files: [
                    {expand: false, src: ["node_modules/jquery/dist/jquery.min.js"], dest: "dist/app/libs/jquery.min.js"}
                ]
            },
            angular: {
                files: [
                    {expand: false, src: ["node_modules/angular/angular.min.js"], dest: "dist/app/libs/angular.min.js"}
                ]
            },
            hammer: {
                files: [
                    {expand: false, src: ["node_modules/hammerjs/hammer.min.js"], dest: "dist/app/libs/hammer.min.js"}
                ]
            },
            materialize: {
                files: [
                    {expand: false, src: ["node_modules/materialize-css/dist/js/materialize.min.js"], dest: "dist/app/libs/materialize.min.js"},
                    {expand: false, src: ["node_modules/materialize-css/dist/css/materialize.min.css"], dest: "dist/app/css/materialize.min.css"},
                    {expand: false, src: ["node_modules/materialize-css/extras/noUiSlider/nouislider.css"], dest: "dist/app/css/nouislider.css"},
                    {expand: true, src: ["node_modules/materialize-css/dist/fonts/roboto/*"], dest: "dist/app/fonts/roboto/", flatten: true}
                ]
            },
            fontawesome: {
                files: [
                    {expand: false, src: ["node_modules/font-awesome/css/font-awesome.min.css"], dest: "dist/app/css/font-awesome.min.css"},
                    {expand: true, src: ["node_modules/font-awesome/fonts/*"], dest: "dist/app/fonts/", flatten: true}
                ]
            },
            sliderDirective: {
                files: [
                    {expand: false, src: ["node_modules/angularjs-slider/dist/rzslider.min.css"], dest: "dist/app/css/rzslider.min.css"}
                ]
            },
            images: {
                files: [
                    {"expand": true, "cwd": "app/img/", "src": ["*"], "dest": "dist/app/img", "flatten": true}
                ]
            },
            json: {
                files: [
                    {"expand": true, "cwd": "app/", "src": ["**/*.json"], "dest": "dist/app/", "flatten": true}
                ]
            },
            style: {
                files: [
                    {"expand": true, "cwd": "app/", "src": ["**/*.css"], "dest": "dist/app/style", "flatten": true}
                ]
            },
        },

        clean: {
            app: ["dist/app"],
            style: ["dist/app/style/"],
            coverage: ["dist/coverage/", "coverage/"],
            doc: ["dist/doc/"],
            package: ["dist/packages/"]
        },

        exec: {
            doc: {
                command: path.resolve("node_modules", ".bin", "esdoc") + " -c esdoc.json",
                stdout: true
            }
        },

        watch: {
            app: {
                files: ["app/**/*.*"],
                tasks: ["quick"],
                options: {
                    spawn: false,
                },
            },
            unit: {
                files: ["app/**/*.spec.js", "app/**/*.*"],
                tasks: ["test"],
                options: {
                    spawn: false,
                },
            },
        },

        ngAnnotate: {
            options: {
                singleQuotes: true,
            },
            app: {
                files: {
                    "dist/app/libs/bundle.js": ["dist/app/libs/bundle.js"]
                },
            }
        },
        'string-replace': {
            inline: {
                files: {
                    "dist/app/testVille.html": ["dist/app/testVille.html"]
                },
                options: {
                    replacements: [
                        {
                            pattern: '!!Version!!',
                            replacement: grunt.file.readJSON('package.json').version
                        }
                    ]
                }
            }
        },
        compress: {
            web: {
                options: {
                    archive: './dist/packages/testVille-web.zip'
                },
                files: [
                    {expand: true, cwd:"./dist/app/", src: ['**/*'], dest: '.'}
                ]
            },
            linux32: {
                options: {
                    archive: './dist/packages/testVille-visualization-linux32.zip'
                },
                files: [
                    {expand: true, cwd:"./dist/packages/TestVille/linux32/", src: ['**/*'], dest: '.'}
                ]
            },
            linux64: {
                options: {
                    archive: './dist/packages/testVille-visualization-linux64.zip'
                },
                files: [
                    {expand: true, cwd:"./dist/packages/TestVille/linux64/", src: ['**/*'], dest: '.'}
                ]
            },

            win32: {
                options: {
                    archive: './dist/packages/testVille-visualization-win32.zip'
                },
                files: [
                    {expand: true, cwd:"./dist/packages/TestVille/win32/", src: ['**/*'], dest: '.'}
                ]
            },
            win64: {
                options: {
                    archive: './dist/packages/testVille-visualization-win64.zip'
                },
                files: [
                    {expand: true, cwd:"./dist/packages/TestVille/win64/", src: ['**/*'], dest: '.'}
                ]
            }
        }

    });

    // tasks
    grunt.registerTask("default", ["build"]);

    grunt.registerTask("build", ["clean:app", "jshint", "browserify", "ngAnnotate:app", "htmlmin", "copy:style", "concat:css", "clean:style", "copy:materialize", "copy:sliderDirective", "copy:json", "copy:hammer", "copy:fontawesome", "copy:angular", "copy:images", "copy:jquery", "copy:license", "string-replace"]);
    grunt.registerTask("test", ["clean:coverage", "mocha_istanbul"]);
    grunt.registerTask("doc", ["clean:doc", "exec:doc"]);
    grunt.registerTask("package", ["clean:package", "nwjs", "force:compress"]);
    grunt.registerTask("quick", ["jshint", "browserify", "ngAnnotate:app", "htmlmin", "copy:style", "concat:css","copy:json", "clean:style", "copy:images", "string-replace"]);


};
