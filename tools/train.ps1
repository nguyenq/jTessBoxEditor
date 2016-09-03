<#

Automate Tesseract 3.02 language data pack generation process.

@author: Quan Nguyen
@date: 16 April 2013

The script file should be placed in the same directory as Tesseract's binary executables.
All training data files must be prefixed with the language code -- such as: 
vie.arial.exp0.tif, vie.font_properties, vie.unicharambigs, vie.frequent_words_list, vie.words_list
-- and placed in a trainfolder directory, which could be placed directly under Tesseract directory.

http://code.google.com/p/tesseract-ocr/wiki/TrainingTesseract3

Run PowerShell as Administrator and allow script execution by running the following command:

PS > Set-ExecutionPolicy RemoteSigned

Then execute the script by:

PS > .\train.ps1
or
PS > .\train.ps1 yourlang trainfolder

Windows PowerShell 2.0 Download: http://support.microsoft.com/kb/968929

#>

if ($args[0] -and ($args[0] -eq "-?" -or $args[0] -eq "-h" -or $args[0] -eq "-help")) {
    Write-Host "Usage: .\train.ps1"
    Write-Host "   or  .\train.ps1 trainfolder yourlang [bootstraplang]"
    Write-Host "where trainfolder directory contains all the training data files prefixed with yourlang, e.g.,"
    Write-Host "vie.arial.exp0.tif, vie.font_properties, vie.unicharambigs, vie.frequent_words_list, vie.words_list,"
    Write-Host "and could be placed directly under Tesseract directory"
    exit
}

$trainDir = $args[0]
if (!$trainDir) {
    $trainDir = Read-Host "Enter location of the training data folder"
}

$lang = $args[1]
if (!$lang) {
    $lang = Read-Host "Enter a language code"
}

if ($lang -eq "" -or $trainDir -eq "") {
     Write-Host "Invalid input"
     exit
}

if (!(test-path $trainDir)) {
    throw "{0} is not a valid path" -f $trainDir
    exit
}

$bootstraplang = $args[2]
if (!$bootstraplang) {
    $bootstraplang = Read-Host "Enter a bootstrap language code (optional)"
}

echo "=== Generating Tesseract language data for language: $lang ==="

$fullPath = Resolve-Path $trainDir
echo "** Your training images should be in ""$fullPath"" directory."

$al = New-Object System.Collections.ArrayList

echo "Make Box Files"
$boxFiles = ""
Foreach ($entry in dir $trainDir) {
   If ($entry.name.toLower().endsWith(".tif") -and $entry.name.startsWith($lang)) {
      echo "** Processing image: $entry"
      $nameWoExt = [IO.Path]::Combine($trainDir, $entry.BaseName)
      $al.Add($nameWoExt)

      If ($bootstraplang -eq "") {
        $trainCmd = ".\tesseract {0}.tif {0} batch.nochop makebox" -f $nameWoExt
      } else {
#Bootstrapping a new character set
        $trainCmd = ".\tesseract {0}.tif {0} -l {1} batch.nochop makebox" -f $nameWoExt, $bootstraplang
      }
     
#Should comment out the next line after done with editing the box files to prevent them from getting overwritten in repeated runs.
      Invoke-Expression $trainCmd
      $boxFiles += $nameWoExt + ".box "
   }
}
echo "** Box files should be edited before continuing. **"

echo "Generate .tr Files"
$trFiles = ""
Foreach ($entry in $al) {
      $trainCmd = ".\tesseract {0}.tif {0} box.train" -f $entry
      Invoke-Expression $trainCmd
      $trFiles += $entry + ".tr "
}

echo "Compute the Character Set"
Invoke-Expression ".\unicharset_extractor -D $trainDir $boxFiles"

echo "set_unicharset_properties"
Invoke-Expression ".\set_unicharset_properties -U unicharset -O unicharset --script_dir=$trainDir";

echo "Clustering"
Invoke-Expression ".\shapeclustering -F $trainDir\$lang.font_properties -U $trainDir\unicharset $trFiles"
Invoke-Expression ".\mftraining -F $trainDir\$lang.font_properties -U $trainDir\unicharset -O $trainDir\$lang.unicharset $trFiles"
move-item -force -path inttemp -destination $trainDir\$lang.inttemp
move-item -force -path pffmtable -destination $trainDir\$lang.pffmtable
#move-item -force -path Microfeat -destination $trainDir\$lang.Microfeat
Invoke-Expression ".\cntraining $trFiles"
move-item -force -path normproto -destination $trainDir\$lang.normproto
move-item -force -path shapetable -destination $trainDir\$lang.shapetable

echo "Dictionary Data"
Invoke-Expression ".\wordlist2dawg $trainDir\$lang.frequent_words_list $trainDir\$lang.freq-dawg $trainDir\$lang.unicharset"
Invoke-Expression ".\wordlist2dawg $trainDir\$lang.words_list $trainDir\$lang.word-dawg $trainDir\$lang.unicharset"
if (test-path $trainDir\$lang.punc) {
    Invoke-Expression ".\wordlist2dawg $trainDir\$lang.punc $trainDir\$lang.punc-dawg $trainDir\$lang.unicharset"
}
if (test-path $trainDir\$lang.numbers) {
    Invoke-Expression ".\wordlist2dawg $trainDir\$lang.numbers $trainDir\$lang.number-dawg $trainDir\$lang.unicharset"
}
if (test-path $trainDir\$lang.word.bigrams) {
    Invoke-Expression ".\wordlist2dawg $trainDir\$lang.word.bigrams $trainDir\$lang.bigram-dawg $trainDir\$lang.unicharset"
}

echo "The last file (unicharambigs) -- this is to be manually edited"
if (!(test-path $trainDir\$lang.unicharambigs)) {
    new-item "$trainDir\$lang.unicharambigs" -type file
    set-content -path $trainDir\$lang.unicharambigs -value "v1"
}

echo "Putting it all together"
Invoke-Expression ".\combine_tessdata $trainDir\$lang."