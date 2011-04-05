<#

Automate Tesseract 3.01 language data pack generation process.

@author: Quan Nguyen
@date: 28 Mar 2011
@version: 1.0

The script file should be placed in the same directory as Tesseract's binary executables.

Run PowerShell as Administrator and allow script execution by running the following command:

PS > Set-ExecutionPolicy RemoteSigned

Then execute the script by:

PS > .\train.ps1
or
PS > .\train.ps1 yourlang imageFolder

If imageFolder is not specified, it is default to a yourlang subdirectory under Tesseract directory.

Windows PowerShell 2.0 Download: http://support.microsoft.com/kb/968929

#>

$lang = $args[0]
if (!$lang) {
    $lang = Read-Host "Enter a language code"
}

$langDir = $lang

if ($args[1]) {
    $langDir = $args[1]
}

if (!(test-path $langDir))
{
    throw "{0} is not a valid path" -f $langDir
}

echo "=== Generating Tesseract language data for language: $lang ==="

$fullPath = [IO.Path]::GetFullPath($langDir)
echo "** Your training images should be in ""$fullPath"" directory."

$al = New-Object System.Collections.ArrayList

echo "Make Box Files"
$boxFiles = ""
Foreach ($entry in dir $langDir) {
   If ($entry.name.toLower().endsWith(".tif") -and $entry.name.startsWith($lang)) {
      echo "** Processing image: $entry"
      $nameWoExt = [IO.Path]::Combine($langDir, $entry.BaseName)
      $al.Add($nameWoExt)

#Bootstrapping a new character set
      $trainCmd = ".\tesseract {0}.tif {0} -l {1} batch.nochop makebox" -f $nameWoExt, $lang
#Should comment out the next line after done with editing the box files to prevent them from getting overwritten in repeated runs.
      Invoke-Expression $trainCmd
      $boxFiles += $nameWoExt + ".box "
   }
}
echo "** Box files should be edited before continuing. **"

echo "Generate .tr Files"
$trFiles = ""
Foreach ($entry in $al) {
      $trainCmd = ".\tesseract {0}.tif {0} nobatch box.train" -f $entry
      Invoke-Expression $trainCmd
      $trFiles += $entry + ".tr "
}

echo "Compute the Character Set"
Invoke-Expression ".\unicharset_extractor -D $langDir $boxFiles"
move-item -force -path $langDir\unicharset -destination $langDir\$lang.unicharset

echo "Clustering"
Invoke-Expression ".\mftraining -F $langDir\$lang.font_properties -U $langDir\$lang.unicharset $trFiles"
move-item -force -path inttemp -destination $langDir\$lang.inttemp
move-item -force -path pffmtable -destination $langDir\$lang.pffmtable
move-item -force -path Microfeat -destination $langDir\$lang.Microfeat

Invoke-Expression ".\cntraining $trFiles"
move-item -force -path normproto -destination $langDir\$lang.normproto

echo "Dictionary Data"
Invoke-Expression ".\wordlist2dawg $langdir\$lang.frequent_words_list.txt $langdir\$lang.freq-dawg $langdir\$lang.unicharset"
Invoke-Expression ".\wordlist2dawg $langdir\$lang.words_list.txt $langdir\$lang.word-dawg $langdir\$lang.unicharset"

echo "The last file (unicharambigs) -- this is to be manually edited"
if (!(test-path $langdir\$lang.unicharambigs)) {
    new-item "$langdir\$lang.unicharambigs" -type file
    set-content -path $langdir\$lang.unicharambigs -value "v1"
}

echo "Putting it all together"
Invoke-Expression ".\combine_tessdata $langdir\$lang."