# Rebase 83c8b53..83c8b53 onto 83c8b53 (1 command(s))
#
# Commands:
# p, pick = use commit
# r, reword = use commit, but edit the commit message
# e, edit = use commit, but stop for amending
# s, squash = use commit, but meld into previous commit
# f, fixup = like "squash", but discard this commit's log message
# x, exec = run command (the rest of the line) using shell
# d, drop = remove commit
#
# These lines can be re-ordered; they are executed from top to bottom.
#
# If you remove a line here THAT COMMIT WILL BE LOST.
#
# However, if you remove everything, the rebase will be aborted.
#
# Note that empty commits are commented out


## Splitting commits depending on the module to which they belong.
## This shall allow only pushing certain modules to remote.

pick 790e4c5c985ab181ae4450a88fa205db1b181784 initial test commit
pick 0687f76c71b2b0f5ea0e8707cc0b0dbbe0bb9d07 Second test commit.
pick f262b2232319b7c394a165879829283ec7ef3926 Third test commit.
pick b331d5784c4113189923cf22012fa952e3ac3261 Add some drafts
pick e756827c2d7143fd2bb82c543b3f72421b94f1f4 LOCAL-1 Add bparser project.
pick d6cc7284ab9bc01eeec159cc729654707a988473 LOCAL-1 Fix project paths.
pick 260983cec01820a98875d3ff1993403aed74cc60 LOCAL-1 Restructure bparser...

## This commit contains bparser and sicc changes -> split it.
edit feffa1350ad03cbc1323ae0e02286828f1b900d3 LOCAL-1 Restructure bparser
# git commit bparser eclipsejprojs -m "LOCAL-1 Restructure bparser (split: feffa1350ad03cbc1323ae0e02286828f1b900d3, bparser)"
# git commit sicc drafts -m "LOCAL-1 Restructure bparser (split: feffa1350ad03cbc1323ae0e02286828f1b900d3, sicc)"

pick 6c49c3deb60d2358eaf6e628fedaa049380e8bae LOCAL-1 Add dodl source
pick 40d29608bc4c576b650a4437f6acd0b84f3b3476 LOCAL-1 Add a script that links all files of a directory to an eclipse project.
pick 3def2ec4cb5fbe0d0f47a2c03d100e0463666c33 LOCAL-1 Get basic dodl reflections test running.

## This commit contains dodl/doml and jutil changes -> split it.
edit 7b1867aeb757f350c5532b398592716e34f4f930 LOCAL-1 Add jutil (and doml around a bit)
# git commit jutil -m "LOCAL-1 Add jutil (and doml around a bit) (split: 7b1867aeb757f350c5532b398592716e34f4f930, jutil)"
# git commit dodl doml -m "LOCAL-1 Add jutil (and doml around a bit) (split: 7b1867aeb757f350c5532b398592716e34f4f930, dodl doml)"

pick 953624c57de5074fe50c9e78054b2fcd065c6c3b LOCAL-1 Dodling around on the command line parser.

## This commit contains dodl and sicc changes -> split it.
edit 0d1bff78b26575b629541a1bacdd44eb3ba7a2be LOCAL-1 Dodling on command line...
# git commit dodl -m "LOCAL-1 Dodling on command line... (split: 0d1bff78b26575b629541a1bacdd44eb3ba7a2be, dodl)
# git commit sicc -m "LOCAL-1 Dodling on command line... (split: 0d1bff78b26575b629541a1bacdd44eb3ba7a2be, sicc)

## This commit contains dodl and .swp changes -> split it.
edit f99902b236cf41e115b288017a664b4efa5e9b78 LOCAL-1 Generate dodl command line parser java files (doesn't compile yet)
#'
# git commit dodl -m "LOCAL-1 Generate dodl command line parser java files (doesn't compile yet) (split: f99902b236cf41e115b288017a664b4efa5e9b78, dodl)"
# git commit .swp -m "LOCAL-1 Generate dodl command line parser java files (doesn't compile yet) (split: f99902b236cf41e115b288017a664b4efa5e9b78, .swp)"

pick 3702169a7bd918486260eb144144e2ea125f1a0d LOCAL-1 Add diagnostic message package
pick cba19c3a659a32063d5a02ce0edc88933b75f1ff LOCAL-1 Move diag code.

## This commit contains dodl and diag changes -> split it.
edit ddc11768987f9e677f908d3aede51ee72de89612 LOCAL-1 Move diag code.
# git commit diag -m "LOCAL-1 Move diag code. (split: ddc11768987f9e677f908d3aede51ee72de89612, diag)
# git commit dodl -m "LOCAL-1 Move diag code. (split: ddc11768987f9e677f908d3aede51ee72de89612, dodl)

## This commit contains dodl and diag changes -> split it.
edit c80fe094ee632c41f9f52fb5624ce945eb502cfe LOCAL-1 Working on dodl command line parser...
# git commit diag -m "LOCAL-1 Working on dodl command line parser... (split: c80fe094ee632c41f9f52fb5624ce945eb502cfe, diag)
# git commit dodl -m "LOCAL-1 Working on dodl command line parser... (split: c80fe094ee632c41f9f52fb5624ce945eb502cfe, dodl)

pick b5f49391fa499dad8c3dfd788eaa9db2e7915473 LOCAL-1 Read command line attributes.

## This commit contains asm and dodl changes -> split it.
edit 79f8c80aa9d4aaf87de9468c90e4b4b6b0383ff2 LOCAL-1 drafting around.
# git commit asm -m "LOCAL-1 drafting around. (split: 79f8c80aa9d4aaf87de9468c90e4b4b6b0383ff2, asm)
# git commit dodl -m "LOCAL-1 drafting around. (split: 79f8c80aa9d4aaf87de9468c90e4b4b6b0383ff2, dodl)

pick 209d5ad41d500d37e02c27f165368c57b16aa802 LOCAL-1 First commit from 64-bit linux.

## Split!
edit 514bc14b3f332da58961d1ed4c19c4f8c3d97222 LOCAL-1 Work on dodl command line reader.
# git commit bparser -m "LOCAL-1 Work on dodl command line reader. (split: 514bc14b3f332da58961d1ed4c19c4f8c3d97222, bparser)
# git commit diag -m "LOCAL-1 Work on dodl command line reader. (split: 514bc14b3f332da58961d1ed4c19c4f8c3d97222, diag)
# git commit dodl -m "LOCAL-1 Work on dodl command line reader. (split: 514bc14b3f332da58961d1ed4c19c4f8c3d97222, dodl)

## Split!
edit 77a2eed5d20e8f9fbec133199e01bcae585c8ff0 LOCAL-1 Work on dodl command line parsing.
# git commit diag -m "LOCAL-1 Work on dodl command line parsing. (split: 77a2eed5d20e8f9fbec133199e01bcae585c8ff0, diag)"
# git commit dodl -m "LOCAL-1 Work on dodl command line parsing. (split: 77a2eed5d20e8f9fbec133199e01bcae585c8ff0, dodl)"

pick 8408ce4437298772cd00437d2734c12a7d69583a LOCAL-1 Work on dodl command line constructors (note: code duplication).
pick 615e06e677d0309ea3f3daa2fd4ed38e95f08671 LOCAL-1 Re-draft sicc stuff (method overload/override).
pick 2d2a927a20782f38558fa397d6a27224df4867ec LOCAL-1 Work on command line parser contructors.
pick 2c9161721ccc51908b493009cb050685e3dd3239 LOCAL-1 drafts
pick 02d1b69e10bf9eba02a1481cb682bdd8867729b4 LOCAL-1 Drafting sicc stuff.
pick 134091250cba9ed639e9651c90545daa5e870607 LOCAL-1 Drafting sicc stuff.
pick f3d441af1171446c0de98bcf9f9683b4dcdbceb8 LOCAL-1 Drafting parsing stages.
pick 2dd1a56c377c690e3c5aff5a13f1c47794b85ca7 LOCAL-1 Add archive code for packages.
pick 74d9d894df3027bd438135045a932fc06a9a0d48 LOCAL-1 Work on archives.
pick 2ab3f43df3a44d426e8003aae2ec2c7b10260f31 LOCAL-1 Add one more archive test file.
pick 2a64a4547240a549198851fcf51ac6f63c0c59fe LOCAL-1 Working on packages.
pick c82988fce09f423f4b239d4578bb182b9fe0b19d LOCAL-1 Use LimitedCountingInputStream in ArMultiFileInputStream. Expecting faster skip (not sure).
pick 166fe3cf52d9b71673b435b47b26e397b1e3baf3 LOCAL-1 Working on packages.
pick 4534e80820b9c3fa96237d2d534a4b30caffe290 LOCAL-1 stuff
pick a8a1778afbe4ff88b6180076848b9f67d60a8796 LOCAL-1 Move archiving (sicc.ar) code from eclipse directory to code directories.
pick c3eb23595ba4e68361765dc3453d46c8615745af LOCAL-1 Import all MPCC and CTYPELIB code and documents.
pick 9dba2dacffaae4fca3bbd7049793f9074cea03a2 LOCAL-1 Create package c (for compiler) to avoid confusion. Actually the toplevel package should be called sic now.
pick 1bc28ec56cb886bfd5c4f5c9c5c9eb39204ff25f LOCAL-1 Working on new typelib and scopes.
pick de8a8f3f2f02ebc504bbfd572a04e37af26af61c LOCAL-1 Working on scopes.
pick c3fc3849e0249729f44668622a8f1e62b740ad59 LOCAL-1 Drafts to access rights/permission/levels and final namespaces.
pick e9fc460b60ad899a4b64fc8758a04ca5050a24e3 LOCAL-1 Just some idea which doesn't work out entirely. reference register coloring.
pick ba52d0ee3c6ebce8169751e0763d4468b3d80ce6 LOCAL-1 Drafting stuff concerning protection of overloads and class specialization.
pick 50ec4d1effc2ae9e93142faaf5f71d49a46120b1 LOCAL-1 Working on scopes. Went into wrong direction.
pick 3607052288ac89df83146f97d6bc5a0e5e798275 LOCAL-1 Working on scopes.
pick 940644e7beb20897b28e242261983107466fe6dd LOCAL-1 working on scopes. (Might need correction.)
pick 1010599 LOCAL-1 working on scopes. Only use the full name of a scope to identify it, because of partial scopes.
pick fefa5d7 LOCAL-1 working on scopes.
pick 9a1f188 LOCAL-1 Add reasons for more fine graned access rights.
pick fcf6cb2 LOCAL-1 stash (sickday)
pick 1f57fa5 LOCAL-1 scopes: RestrictedAccessRights.
pick 60689c5 LOCAL-1 Adapt sicc yacc/lex sources to new package names.
pick 921321a LOCAL-1 stuff...
pick 4474eab LOCAL-1 Draft some stuff.


## Split
edit b851e358ed42de0487950554052061679a277b52 LOCAL-1 Trying to design a functional language.
# git commit functional -m "LOCAL-1 Trying to design a functional language. (split: b851e358ed42de0487950554052061679a277b52, functional)"
# git commit jtemplates -m "LOCAL-1 Trying to design a functional language. (split: b851e358ed42de0487950554052061679a277b52, jtemplates)"


pick 6f725bb LOCAL-1 sic.functional: Relaxing grammar of compound statments (shouldn't introduce ambiguities - they already existed).
pick b6ed17f LOCAL-1 Re-designing hardware description language.
pick 3c1fc6f LOCAL-1 Add to sic.hdl draft.
pick 0a2375a LOCAL-1 some more SIC.HDL drafts.
pick 2f3fea4 LOCAL-1 Start a CHDL grammar.
pick 3369013 LOCAL-1 some time ago I wrote some wiring conventions... but they might not make sense.
pick c4d017f LOCAL-1 Add SIC runtime environment.
pick ea2de6f LOCAL-1 Add SIC runtime environment.
pick 4807c9b LOCAL-1 Add SIC runtime environment.
pick b3a6e4a LOCAL-1 Add SIC runtime environment.
pick ec80a4e LOCAL-1 add sice bitset.
pick 47b8071 LOCAL-1 add sice bitset.
pick db4ece7 LOCAL-1 work on sice bitset.
pick 228347f LOCAL-1 sice bitset: Avoid code duplication and hope that compiler optimizes this.
pick 12491a2 LOCAL-1 sice: add some stuff that failed.
pick bba5193 LOCAL-1 Commit some hdl example I once wrote...
pick 83c8b53 LOCAL-1 Add one of these readme.md files for sic.bparser.

