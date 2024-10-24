import os
import shutil
from datetime import datetime
import logging
from configparser import ConfigParser
import zipfile


# Instantiate ConfigParser().
cfg = ConfigParser()
cfg.read(f"{os.path.abspath(os.path.dirname(__file__))}\\config\\cfg.ini")

# Instantiate logging.
logging.basicConfig(datefmt="%Y-%m-%d %H:%M")
logger = logging.getLogger(f"{os.path.abspath(os.path.dirname(__file__))}\\logs\\logger.log")
logger.setLevel(logging.DEBUG)
log_format = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s", "%Y-%m-%d %H:%M")
filename = f"{os.path.abspath(os.path.dirname(__file__))}\\logs\\logger.log"
log_handler = logging.FileHandler(filename)
log_handler.setLevel(logging.DEBUG)
log_handler.setFormatter(log_format)
logger.addHandler(log_handler)

def Extract_Archive(str_Zip_Path, str_csv_path):
	"""
    A method that extracts all .7z files from str_Zip_Path into a new folder(which has to be created).
    :param str_Zip_Path: Path to the archives
    :param str_csv_path: A new folder(that has to be created) where to save the CSVs from .7z.
    :return: status -> OK(files extracted successfully), not OK(Failed to extract files)
    """
	error = "No Error"
	try:
		with zipfile.ZipFile(str_Zip_Path, mode="r") as zip_file:
			os.chdir(str_csv_path)
			zip_file.extractall()   # Unzip all archives in current folder
	except Exception as e:
		error = e
		print('ZIP_Driver\Extract_Archive - Error: Arhive extraction faild:' + str(e))
		return error
	else:
		return error


def copy_if_newer_or_missing(source, dest):
	"""
    Checks everything from the two given directories paths.
    :param source: source directory path.
    :param dest: destination directory path.
    :return: path of source_files that need to be moved from source to dest.
    """
	# Show last synchronization date.
	print("INFO: Last Synch: "+str(cfg["SYNCHRONIZE"]["last_synch"]))
	logger.info("Last Synch: "+str(cfg["SYNCHRONIZE"]["last_synch"]))
	cfg["SYNCHRONIZE"]["last_synch"] = datetime.strftime(datetime.now(), "%d.%m.%Y %H:%M")
	# Write new synch date.
	with open(f"{os.path.abspath(os.path.dirname(__file__))}\\config\\cfg.ini", "w") as cfg_file:
		cfg.write(cfg_file)
	# Make sure that both `source` and `dest` folders exist.
	if not os.path.exists(source):
		raise FileNotFoundError(f"Source folder '{source}' does not exist.")
	if not os.path.exists(dest):
		os.makedirs(dest)

	# Walk through the source folder.
	for root, dirs, files in os.walk(source):
		# Create the relative path of the `dest` using `source`.
		relative_path = os.path.relpath(root, source)
		dest_root = os.path.join(dest, relative_path)

		# Create the directories that don't exist into `dest`.
		if not os.path.exists(dest_root):
			os.makedirs(dest_root)

		# Create paths.
		for dir_name in dirs:
			source_dir = os.path.join(root, dir_name)
			dest_dir = os.path.join(dest_root, dir_name)

			# If directory doesn't exist in `dest`, copy it.
			if not os.path.exists(dest_dir):
				shutil.copytree(source_dir, dest_dir)
				print(f"INFO: Copied new directory: {source_dir} to {dest_dir}.")
				logger.info(f"Copied new directory: {source_dir} to {dest_dir}.")

		# Handle files.
		for file_name in files:
			source_file = os.path.join(root, file_name)
			dest_file = os.path.join(dest_root, file_name)

			# If file is missing in `dest` or is outdated, copy it.
			if not os.path.exists(dest_file):
				shutil.copy2(source_file, dest_file)
				print(f"INFO: Copied new file: {source_file} to {dest_file}.")
				logger.info(f"Copied new file: {source_file} to {dest_file}.")
			else:
				# Check which file is newer and if it's newer in `source` update it to `dest`.
				source_time = os.path.getmtime(source_file)
				dest_time = os.path.getmtime(dest_file)
				if source_time > dest_time:
					shutil.copy2(source_file, dest_file)
					print(f"INFO: Updated file: {source_file} to {dest_file}.")
					logger.info(f"Updated file: {source_file} to {dest_file}.")


if __name__ == "__main__":
	Extract_Archive(os.path.join(cfg["PATHS"]["source"],'L2JFrozen_15.zip'),cfg["PATHS"]["source"])
	copy_if_newer_or_missing(cfg["PATHS"]["source"],
							 cfg["PATHS"]["dest"])







if __name__ == "__main__":
	# Unzip

	Extract_Archive(os.path.join(cfg["PATHS"]["source"],'L2JFrozen_15.zip'),cfg["PATHS"]["source"])
	directories_check(cfg["PATHS"]["source"],
					  cfg["PATHS"]["dest"])
